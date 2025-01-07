package files.simulation;

import files.map_elements.Animal;
import files.map_elements.WorldElement;
import files.maps.MapChangeListener;
import files.maps.WorldMap;
import files.maps.variants.FullPredestinationEquatorMap;
import files.maps.variants.FullPredestinationLiveGivingCorpseMap;
import files.maps.variants.OldnessSadnessEquatorMap;
import files.maps.variants.OldnessSadnessLiveGivingCorpseMap;
import files.util.Boundary;
import files.util.Vector2d;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class SimulationPresenter implements MapChangeListener {

    private WorldMap worldMap;
    private int updateCount = 0;
    private int simulationCount = 1;

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

//    @FXML
//    public void initialize() {
//        equatorFlag.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) { // Jeśli equatorFlag jest zaznaczony
//                liveGivingCorpseFlag.setSelected(false); // Odznacz liveGivingCorpseFlag
//            } else { // Jeśli equatorFlag jest odznaczony
//                liveGivingCorpseFlag.setSelected(true); // Zaznacz liveGivingCorpseFlag
//            }
//        });
//
//        liveGivingCorpseFlag.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) { // Jeśli liveGivingCorpseFlag jest zaznaczony
//                equatorFlag.setSelected(false); // Odznacz equatorFlag
//            } else { // Jeśli liveGivingCorpseFlag jest odznaczony
//                equatorFlag.setSelected(true); // Zaznacz equatorFlag
//            }
//        });
//
//        oldnessSadnessFlag.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) { // Jeśli oldnessSadnessFlag jest zaznaczony
//                fullPredestinationFlag.setSelected(false); // Odznacz fullPredestinationFlag
//            } else { // Jeśli oldnessSadnessFlag jest odznaczony
//                fullPredestinationFlag.setSelected(true); // Zaznacz fullPredestinationFlag
//            }
//        });
//
//        fullPredestinationFlag.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) { // Jeśli fullPredestinationFlag jest zaznaczony
//                oldnessSadnessFlag.setSelected(false); // Odznacz oldnessSadnessFlag
//            } else { // Jeśli fullPredestinationFlag jest odznaczony
//                oldnessSadnessFlag.setSelected(true); // Zaznacz oldnessSadnessFlag
//            }
//        });
//
//    }

    public void setWorldMap(WorldMap map) {
        this.worldMap = map;
    }
    public void drawMap() {
        // Czyszczenie siatki
        int CELL_WIDTH = 30;
        int CELL_HEIGHT = 30;
        double cellContentSizeMultiplier = 0.8;

        clearGrid();
        Boundary bounds = worldMap.getCurrentBounds();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();
        int rows = upperRight.getY() - lowerLeft.getY() + 1;
        int columns = upperRight.getX() - lowerLeft.getX() + 1;

        for (int i = 0; i < columns; i++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(CELL_WIDTH));
        }
        for (int i = 0; i < rows; i++) {
            mapGrid.getRowConstraints().add(new RowConstraints(CELL_HEIGHT));
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
                        imageView.setFitWidth(CELL_WIDTH*cellContentSizeMultiplier); // Szerokość komórki
                        imageView.setFitHeight(CELL_HEIGHT*cellContentSizeMultiplier); // Wysokość komórki
                        GridPane.setHalignment(imageView, HPos.CENTER);

                        if (element instanceof Animal) {
                            imageView.setOnMouseClicked(event -> showAnimalInfo((Animal) element));
                        }

                        mapGrid.add(imageView, x - lowerLeft.getX(), upperRight.getY() - y);

                    } else {
                        Rectangle emptyCell = new Rectangle(CELL_WIDTH*element.getElementSizeMultiplier(), CELL_HEIGHT*element.getElementSizeMultiplier());
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
                SimulationParams initParams = getSimulationParams();

                WorldMap map = null;
                if (initParams.fullPredestinationFlag() &&  initParams.equatorFlag())
                    map = new FullPredestinationEquatorMap(initParams);
                if (initParams.fullPredestinationFlag() && initParams.liveGivingCorpseFlag())
                    map = new FullPredestinationLiveGivingCorpseMap(initParams);
                if ( initParams.oldnessSadnessFlag() &&  initParams.equatorFlag())
                    map = new OldnessSadnessEquatorMap(initParams);
                if ( initParams.oldnessSadnessFlag() && initParams.liveGivingCorpseFlag())
                    map = new OldnessSadnessLiveGivingCorpseMap(initParams);

                newPresenter.setWorldMap(map);
                assert map != null;
                map.addObserver(newPresenter);

                WorldMap finalMap = map;
                Thread simulationThread = new Thread(() -> {
                    try {
                        Simulation newSimulation = new Simulation(initParams, finalMap);
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
}


