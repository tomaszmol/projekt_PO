package files.simulation;
import files.map_elements.Animal;
import files.map_elements.WorldElement;
import files.maps.*;
import files.util.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class SimulationPresenter implements MapChangeListener {
    private WorldMap worldMap;
    private int updateCount = 0;
    private int simulationCount = 1;

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
    private CheckBox ageFlag;
    @FXML
    private CheckBox corpseFlag;
    @FXML
    private CheckBox globeFlag;

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
                if (!initParams.circleOfLifeFlag() &&  initParams.earthMapFlag()) map = new EarthEquatorMap(initParams.mapWidth(),initParams.mapHeight(),(int)(initParams.mapHeight()*0.2));
                if (!initParams.circleOfLifeFlag() && !initParams.earthMapFlag()) map = new RectEquatorMap(initParams.mapWidth(),initParams.mapHeight(),(int)(initParams.mapHeight()*0.2));
                if ( initParams.circleOfLifeFlag() &&  initParams.earthMapFlag()) map = new EarthCircleOfLifeMap(initParams.mapWidth(),initParams.mapHeight());
                if ( initParams.circleOfLifeFlag() && !initParams.earthMapFlag()) map = new RectCircleOfLifeMap(initParams.mapWidth(),initParams.mapHeight());

                newPresenter.setWorldMap(map);
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

        ageFlag.setSelected(true);
        corpseFlag.setSelected(true);
        globeFlag.setSelected(true);
    }
    SimulationParams getSimulationParams(){
        try {
            // Pobieranie danych liczbowych z pól tekstowych
            int mapHeight = Integer.parseInt(mapHeightField.getText().trim());
            int mapWidth = Integer.parseInt(mapWidthField.getText().trim());
//            int jungleHeight = (int)(mapHeight * jungleHeightSlider.getValue());
//            int jungleWidth = (int)(mapWidth * jungleWidthSlider.getValue());
            int plantEnergyProfit = Integer.parseInt(plantEnergyField.getText().trim());
            int minCopulationEnergy = Integer.parseInt(copulationEnergyField.getText().trim());
            int initialAnimalEnergy = Integer.parseInt(initialAnimalEnergyField.getText().trim());
            int dailyAnimalEnergy = Integer.parseInt(dailyAnimalEnergyField.getText().trim());
            int initialAnimalsOnMap = Integer.parseInt(initialAnimalsField.getText().trim());
            int dailyPlantSpawns = Integer.parseInt(dailyPlantSpawnsField.getText().trim());
            int simSteps = Integer.parseInt(simulationSteps.getText().trim());

            // Pobieranie wartości boolean z checkboxów
            boolean ageFlagValue = ageFlag.isSelected();
            boolean corpseFlagValue = corpseFlag.isSelected();
            boolean globeFlagValue = globeFlag.isSelected();

            // Tworzenie obiektu SimulationParams
            return new SimulationParams(
                    mapHeight, mapWidth,
                    plantEnergyProfit, minCopulationEnergy, initialAnimalEnergy,
                    dailyAnimalEnergy, initialAnimalsOnMap, dailyPlantSpawns,
                    ageFlagValue, corpseFlagValue, globeFlagValue, simSteps
            );
        } catch (NumberFormatException e) {
            System.err.println("Invalid parameters! Please provide valid numeric values.");
        }
        return null;
    }

    private void showAnimalInfo(Animal animal) {
        animalInfoLabel.setText(animal.getAnimalInfo());
        animalInfoBoxRight.setVisible(true);
    }
}


