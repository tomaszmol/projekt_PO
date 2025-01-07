package files.simulation;

import files.map_elements.Animal;
import files.map_elements.StatisticsTracker;
import files.map_elements.WorldElement;
import files.maps.MapChangeListener;
import files.maps.WorldMap;
import files.maps.variants.FullPredestinationEquatorMap;
import files.maps.variants.FullPredestinationLiveGivingCorpseMap;
import files.maps.variants.OldnessSadnessEquatorMap;
import files.maps.variants.OldnessSadnessLiveGivingCorpseMap;
import files.util.Boundary;
import files.util.Vector2d;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationPresenter implements MapChangeListener {

    private WorldMap worldMap;
    private int updateCount = 0;
    private int simulationCount = 1;
    SimulationParams params;
    StatisticsTracker statsTracker;

    @FXML
    public TextField geneNumber;
    @FXML
    public TextField geneMutationChance;
    @FXML
    public VBox animalInfoBoxRight;
    @FXML
    public Label animalInfoLabel;
    @FXML
    public Label dominantGene;
    @FXML
    private TextField simulationSteps;
    @FXML
    private GridPane mapGrid;  // Powiązanie z kontrolką w FXML
    @FXML
    private Label moveDescriptionLabel;  // Powiązanie z kontrolką w FXML
    @FXML
    private Label updateCountLabel;  // Powiązanie z kontrolką w FXML
    @FXML
    private TextField mapHeightField;
    @FXML
    private TextField mapWidthField;
    @FXML
    private TextField plantEnergyField;
    @FXML
    private TextField copulationEnergyField;
    @FXML
    private TextField initialAnimalEnergyField;
    @FXML
    private TextField dailyAnimalEnergyField;
    @FXML
    private TextField initialAnimalsField;
    @FXML
    private TextField dailyPlantSpawnsField;
    @FXML
    private CheckBox oldnessSadnessFlag;
    @FXML
    private CheckBox liveGivingCorpseFlag;
    @FXML
    public CheckBox equatorFlag;
    @FXML
    public CheckBox fullPredestinationFlag;

    @FXML
    public LineChart<Number, Number> populationChart;
    String[] graph1Series = { "animals", "plants"};

    @FXML
    public LineChart<Number, Number> animalDataChart;
    String[] graph2Series = { "energy" };

    @FXML
    public LineChart<Number, Number> geneticsChart;
    String[] graph3Series = {  };



    public void setWorldMap(WorldMap map) {
        this.worldMap = map;
    }
    public void drawMap() {
        // Czyszczenie siatki
        clearGrid();

        // nowa siatka
        int cellSize = 50;

        // nie dziala za bardzo
        if (params != null) {
            cellSize = (int) Math.min(
                    mapGrid.getScene().getX() / params.mapWidth(),
                    mapGrid.getScene().getY() / params.mapHeight()
            );
            System.out.println(mapGrid.getScene().getX() / params.mapWidth());
            System.out.println(mapGrid.getScene().getY() / params.mapHeight());
        }

        double cellContentSizeMultiplier = 0.8;

        // dane
        Boundary bounds = worldMap.getCurrentBounds();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();
        int rows = upperRight.getY() - lowerLeft.getY() + 1;
        int columns = upperRight.getX() - lowerLeft.getX() + 1;
        for (int i = 0; i < columns; i++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        }
        for (int i = 0; i < rows; i++) {
            mapGrid.getRowConstraints().add(new RowConstraints(cellSize));
        }

        // Rysowanie obiektów na siatce
        for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {

            for (int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
                Vector2d position = new Vector2d(x, y);
                WorldElement element = worldMap.objectAt(position);

                if(element != null){
                    Image img = element.getImage();
                    if (img != null) {
                        ImageView imageView = new ImageView(img);
                        imageView.setFitWidth(cellSize*cellContentSizeMultiplier); // Szerokość komórki
                        imageView.setFitHeight(cellSize*cellContentSizeMultiplier); // Wysokość komórki
                        GridPane.setHalignment(imageView, HPos.CENTER);

                        if (element instanceof Animal) {
                            imageView.setOnMouseClicked(event -> showAnimalInfo((Animal) element));
                        }

                        mapGrid.add(imageView, x - lowerLeft.getX(), upperRight.getY() - y);

                    } else {
                        Rectangle emptyCell = new Rectangle(cellSize*element.getElementSizeMultiplier(), cellSize*element.getElementSizeMultiplier());
                        emptyCell.setFill(element.getElementColour());
                        GridPane.setHalignment(emptyCell, HPos.CENTER);
                        mapGrid.add(emptyCell, x - lowerLeft.getX(), upperRight.getY() - y);
                    }
                }
            }

        }
    }
    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().getFirst()); // Zachowaj linie siatki
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }
    @Override
    public synchronized void mapChanged(WorldMap worldMap, String message) {
        updateCount++;
        // Aktualizacja UI w wątku graficznym
        Platform.runLater(() -> {
            drawMap();
            printGraphs();
            moveDescriptionLabel.setText((message != null) ? message : "No message");
            updateCountLabel.setText("Update count: #" + updateCount);
        });
    }


    @FXML
    public void onSimulationStartClicked() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulationWindow.fxml"));
                BorderPane newSimulationRoot = loader.load();
                SimulationPresenter newPresenter = loader.getController();
                params = getSimulationParams();

                WorldMap map = null;
                if (params.fullPredestinationFlag() &&  params.equatorFlag())
                    map = new FullPredestinationEquatorMap(params);
                if (params.fullPredestinationFlag() && params.liveGivingCorpseFlag())
                    map = new FullPredestinationLiveGivingCorpseMap(params);
                if ( params.oldnessSadnessFlag() &&  params.equatorFlag())
                    map = new OldnessSadnessEquatorMap(params);
                if ( params.oldnessSadnessFlag() && params.liveGivingCorpseFlag())
                    map = new OldnessSadnessLiveGivingCorpseMap(params);

                newPresenter.setWorldMap(map);
                assert map != null;
                map.addObserver(newPresenter);

                setupCharts();

                WorldMap finalMap = map;
                Thread simulationThread = new Thread(() -> {
                    try {
                        Simulation newSimulation = new Simulation(params, finalMap, statsTracker);
                        newSimulation.run();
                    } catch (Exception e) {
                        System.err.println("Simulation thread error! + " + e.getMessage());
                    }
                });
                simulationThread.setDaemon(true);
                simulationThread.start();

                // Tworzenie nowego okna
                Stage newStage = new Stage();
                newStage.setScene(new Scene(newSimulationRoot));
                newStage.setTitle("Simulation Window #" + simulationCount++);
                newStage.show();

            } catch (IOException e) {
                System.err.println("Simulation failed to initialize! + " + e.getMessage());
            }
            if (animalInfoBoxRight != null)
                animalInfoBoxRight.setVisible(false);
    }

    public void initializeSimulationParams() {
        // Ustawianie wartości domyślnych
        mapHeightField.setText("10");
        mapWidthField.setText("10");
        plantEnergyField.setText("20");
        copulationEnergyField.setText("15");
        initialAnimalEnergyField.setText("100");
        dailyAnimalEnergyField.setText("1");
        initialAnimalsField.setText("3");
        dailyPlantSpawnsField.setText("5");
        simulationSteps.setText("100");
        geneMutationChance.setText("0.02");
        geneNumber.setText("6");
        oldnessSadnessFlag.setSelected(false);
        liveGivingCorpseFlag.setSelected(false);
        fullPredestinationFlag.setSelected(true);
        equatorFlag.setSelected(true);
    }

    SimulationParams getSimulationParams(){
        try {
            // Pobieranie danych liczbowych z pól tekstowych
            int mapHeight = Integer.parseInt(mapHeightField.getText().trim());
            int mapWidth = Integer.parseInt(mapWidthField.getText().trim());
            int plantEnergyProfit = Integer.parseInt(plantEnergyField.getText().trim());
            int minCopulationEnergy = Integer.parseInt(copulationEnergyField.getText().trim());
            int initialAnimalEnergy = Integer.parseInt(initialAnimalEnergyField.getText().trim());
            int dailyAnimalEnergy = Integer.parseInt(dailyAnimalEnergyField.getText().trim());
            int initialAnimalsOnMap = Integer.parseInt(initialAnimalsField.getText().trim());
            int dailyPlantSpawns = Integer.parseInt(dailyPlantSpawnsField.getText().trim());
            int simSteps = Integer.parseInt(simulationSteps.getText().trim());
            int geneNum = Integer.parseInt(geneNumber.getText().trim());
            double mutationChance = Double.parseDouble(geneMutationChance.getText().trim());

            // Pobieranie wartości boolean z checkboxów
            boolean fullPredestinationFlagValue = fullPredestinationFlag.isSelected();
            boolean oldnessSadnessFlagValue = oldnessSadnessFlag.isSelected();
            boolean equatorFlagValue = equatorFlag.isSelected();
            boolean liveGivingCorpseFlagValue = liveGivingCorpseFlag.isSelected();

            // Tworzenie obiektu SimulationParams
            return new SimulationParams(
                    mapHeight, mapWidth,
                    plantEnergyProfit, minCopulationEnergy, initialAnimalEnergy,
                    dailyAnimalEnergy, initialAnimalsOnMap, dailyPlantSpawns, fullPredestinationFlagValue,
                    oldnessSadnessFlagValue, equatorFlagValue, liveGivingCorpseFlagValue,simSteps,
                    mutationChance, geneNum
            );
        } catch (NumberFormatException e) {
            System.err.println("Invalid parameters! Please provide valid numeric values.");
        }
        return null;
    }

    private void showAnimalInfo(Animal animal) {
        animalInfoLabel.setText(animal.getAnimalInfo());
        if (animalInfoBoxRight != null) animalInfoBoxRight.setVisible(true);
    }

    private void setupCharts() {
        statsTracker = new StatisticsTracker();
        System.out.println("Setup series!");
        for (String s : graph1Series) statsTracker.addSeries(s);
        for (String s : graph2Series) statsTracker.addSeries(s);
        for (String s : graph3Series) statsTracker.addSeries(s);
    }

    void printGraphs(){
        if(statsTracker == null) setupCharts();
        for (int i=0; i<graph1Series.length; i++) printGraph(populationChart,graph1Series[i],i);
        for (int i=0; i<graph2Series.length; i++) printGraph(animalDataChart,graph2Series[i],i);
        for (int i=0; i<graph3Series.length; i++) printGraph(geneticsChart,graph3Series[i],i);
    }
    void printGraph(LineChart<Number,Number> chart, String seriesName, int seriesNum){
        System.out.println("Printing graph " + seriesName);

        // rozwiaz problem
        List<Number> data = statsTracker.getData(seriesName);

        System.out.println("     " + data);

        // wprowadz nowe dane
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < data.size(); i++) {
            series.getData().add(new XYChart.Data<>(i, data.get(i)));
        }

        // usun stare dane i dodaj nowe
        if (!chart.getData().isEmpty()) chart.getData().remove(seriesNum);
        chart.getData().add(seriesNum,series);
    }
}


