package files.simulation;

import files.map_elements.StatisticsTracker;
import files.maps.WorldMap;
import files.maps.variants.FullPredestinationEquatorMap;
import files.maps.variants.FullPredestinationLiveGivingCorpseMap;
import files.maps.variants.OldnessSadnessEquatorMap;
import files.maps.variants.OldnessSadnessLiveGivingCorpseMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParameterPresenter {

    private int simulationCount = 1;
    SimulationParams params;

    @FXML
    public TextField minCopulationEnergyField;
    @FXML
    public TextField geneNumber;
    @FXML
    private TextField simulationSteps;
    @FXML
    private TextField mapHeightField;
    @FXML
    private TextField mapWidthField;
    @FXML
    private TextField plantEnergyField;
    @FXML
    private TextField copulationEnergyUseField;
    @FXML
    private TextField initialAnimalEnergyField;
    @FXML
    private TextField energyCostPerMoveField;
    @FXML
    private TextField initialAnimalsField;
    @FXML
    private TextField dailyPlantSpawnsField;
    @FXML
    private TextField waitingTimeBetweenMoves;
    @FXML
    private TextField initialPlantNumberField;
    @FXML
    private CheckBox oldnessSadnessFlag;
    @FXML
    private CheckBox liveGivingCorpseFlag;
    @FXML
    public CheckBox equatorFlag;
    @FXML
    public CheckBox fullPredestinationFlag;
    @FXML
    public TextField maxGeneMutationNum;
    @FXML
    public TextField minGeneMutationNum;

    private final Map<TextField, double[]> fieldConstraints = new HashMap<>();

    @FXML
    public void initialize() {
        // Ustawianie wartości domyślnych i zakresu
        mapHeightField.setText("16");
        fieldConstraints.put(mapHeightField, new double[]{1, 50});

        mapWidthField.setText("16");
        fieldConstraints.put(mapWidthField, new double[]{1, 50});

        plantEnergyField.setText("20");
        fieldConstraints.put(plantEnergyField, new double[]{0, 1000});

        copulationEnergyUseField.setText("50");
        fieldConstraints.put(copulationEnergyUseField, new double[]{0, 1000});

        minCopulationEnergyField.setText("100");
        fieldConstraints.put(minCopulationEnergyField, new double[]{0, 1000});

        initialAnimalEnergyField.setText("100");
        fieldConstraints.put(initialAnimalEnergyField, new double[]{0, 1000});

        energyCostPerMoveField.setText("1");
        fieldConstraints.put(energyCostPerMoveField, new double[]{0, 1000});

        initialAnimalsField.setText("10");
        fieldConstraints.put(initialAnimalsField, new double[]{0, 100});

        dailyPlantSpawnsField.setText("4");
        fieldConstraints.put(dailyPlantSpawnsField, new double[]{0, 100});

        simulationSteps.setText("100");
        fieldConstraints.put(simulationSteps, new double[]{1, 10000});

        minGeneMutationNum.setText("0");
        fieldConstraints.put(minGeneMutationNum, new double[]{0, 20});

        maxGeneMutationNum.setText("6");
        fieldConstraints.put(minGeneMutationNum, new double[]{0, 20});

        geneNumber.setText("6");
        fieldConstraints.put(geneNumber, new double[]{1, 20});

        initialPlantNumberField.setText("50");
        fieldConstraints.put(initialPlantNumberField, new double[]{0, 1000});

        waitingTimeBetweenMoves.setText("100");
        fieldConstraints.put(waitingTimeBetweenMoves, new double[]{10, 500});

        oldnessSadnessFlag.setSelected(false);
        liveGivingCorpseFlag.setSelected(false);
        fullPredestinationFlag.setSelected(true);
        equatorFlag.setSelected(true);

        // Definiowanie zakresów dla pól
    }

    @FXML
    public boolean validateInputs() {

        // custom moving constraints
        fieldConstraints.replace(minCopulationEnergyField, new double[]{Integer.parseInt(copulationEnergyUseField.getText().trim()), 1000});
        fieldConstraints.replace(minGeneMutationNum, new double[]{0, Integer.parseInt(geneNumber.getText().trim())});
        fieldConstraints.replace(maxGeneMutationNum, new double[]{Integer.parseInt(minGeneMutationNum.getText().trim()), Integer.parseInt(geneNumber.getText().trim())});

        // Walidacja wartości pól tekstowych
        AtomicBoolean valid = new AtomicBoolean(true);
        fieldConstraints.forEach((field, range) -> {
            try {
                double value = Double.parseDouble(field.getText().trim());
                if (value < range[0] || value > range[1]) {
                    field.setStyle("-fx-border-color: red;"); // Podświetlenie na czerwono
                    valid.set(false);
                } else {
                    field.setStyle(""); // Usunięcie stylu błędu
                }
            } catch (NumberFormatException e) {
                field.setStyle("-fx-border-color: red;"); // Podświetlenie dla niepoprawnych danych
            }
        });
        return valid.get();
    }

    @FXML
    public void onSimulationStartClicked() {
        try {
            if (!validateInputs()) {return;}

            // Initialize new FXML presenter
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulationWindow.fxml"));
            BorderPane newSimulationRoot = loader.load();
            SimulationPresenter newPresenter = loader.getController();

            // Get all data
            params = getSimulationParams();
            StatisticsTracker tracker = new StatisticsTracker();

            // Select simulation map
            WorldMap map = selectSimulationMap();

            // Start new simulation thread
            Thread simulationThread = new Thread(() -> {
                try {
                    Simulation newSimulation = new Simulation(params, map, tracker);
                    newPresenter.setSimulationData(params, map, tracker, newSimulation);
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    SimulationParams getSimulationParams() {
        try {
            // Pobieranie danych z pól tekstowych
            int mapHeight = Integer.parseInt(mapHeightField.getText().trim());
            int mapWidth = Integer.parseInt(mapWidthField.getText().trim());
            int plantEnergyProfit = Integer.parseInt(plantEnergyField.getText().trim());
            int copulationEnergyUse = Integer.parseInt(copulationEnergyUseField.getText().trim());
            int minCopulationEnergy = Integer.parseInt(minCopulationEnergyField.getText().trim());
            int initialAnimalEnergy = Integer.parseInt(initialAnimalEnergyField.getText().trim());
            int energyCostPerMove = Integer.parseInt(energyCostPerMoveField.getText().trim());
            int initialAnimalsOnMap = Integer.parseInt(initialAnimalsField.getText().trim());
            int dailyPlantSpawns = Integer.parseInt(dailyPlantSpawnsField.getText().trim());
            int simSteps = Integer.parseInt(simulationSteps.getText().trim());
            int geneNum = Integer.parseInt(geneNumber.getText().trim());
            int minMutations = Integer.parseInt(minGeneMutationNum.getText().trim());
            int maxMutations = Integer.parseInt(maxGeneMutationNum.getText().trim());
            int waitingTime = Integer.parseInt(waitingTimeBetweenMoves.getText().trim());
            int initialPlantNumber = Integer.parseInt(initialPlantNumberField.getText().trim());

            boolean fullPredestinationFlagValue = fullPredestinationFlag.isSelected();
            boolean oldnessSadnessFlagValue = oldnessSadnessFlag.isSelected();
            boolean equatorFlagValue = equatorFlag.isSelected();
            boolean liveGivingCorpseFlagValue = liveGivingCorpseFlag.isSelected();

            // Tworzenie obiektu SimulationParams
            return new SimulationParams(
                    mapHeight, mapWidth,
                    plantEnergyProfit, minCopulationEnergy, initialAnimalEnergy,
                    energyCostPerMove, initialAnimalsOnMap, dailyPlantSpawns, fullPredestinationFlagValue,
                    oldnessSadnessFlagValue, equatorFlagValue, liveGivingCorpseFlagValue, simSteps,
                    geneNum, waitingTime, initialPlantNumber, copulationEnergyUse, minMutations, maxMutations
            );
        } catch (NumberFormatException e) {
            System.err.println("Invalid parameters! Please provide valid numeric values.");
        }
        return null;
    }

    WorldMap selectSimulationMap() throws Exception {
        if (params.fullPredestinationFlag() && params.equatorFlag())
            return new FullPredestinationEquatorMap(params);
        if (params.fullPredestinationFlag() && params.liveGivingCorpseFlag())
            return new FullPredestinationLiveGivingCorpseMap(params);
        if (params.oldnessSadnessFlag() && params.equatorFlag())
            return new OldnessSadnessEquatorMap(params);
        if (params.oldnessSadnessFlag() && params.liveGivingCorpseFlag())
            return new OldnessSadnessLiveGivingCorpseMap(params);

        throw new Exception("No Map Selected!");
    }
}
