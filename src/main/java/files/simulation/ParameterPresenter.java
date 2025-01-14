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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;

public class ParameterPresenter {

    private int simulationCount = 1;
    SimulationParams params;

    @FXML
    public TextField geneNumber;
    @FXML
    public TextField geneMutationChance;
    @FXML
    private TextField simulationSteps;
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
    public void onSimulationStartClicked() {
            try {
                // initialize new fxml presenter
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("simulationWindow.fxml"));
                BorderPane newSimulationRoot = loader.load();
                SimulationPresenter newPresenter = loader.getController();

                // get all data
                params = getSimulationParams();
                StatisticsTracker tracker = new StatisticsTracker();

                // select simulation map
                WorldMap map = selectSimulationMap();

                // start new simulation thread
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

    @FXML
    public void initialize() {
        // Ustawianie wartości domyślnych
        mapHeightField.setText("10");
        mapWidthField.setText("10");
        plantEnergyField.setText("20");
        copulationEnergyField.setText("15");
        initialAnimalEnergyField.setText("100");
        energyCostPerMoveField.setText("1");
        initialAnimalsField.setText("3");
        dailyPlantSpawnsField.setText("5");
        simulationSteps.setText("100");
        geneMutationChance.setText("0.02");
        geneNumber.setText("6");
        oldnessSadnessFlag.setSelected(false);
        liveGivingCorpseFlag.setSelected(false);
        fullPredestinationFlag.setSelected(true);
        equatorFlag.setSelected(true);
        waitingTimeBetweenMoves.setText("100");
        initialPlantNumberField.setText("10");
    }

    SimulationParams getSimulationParams(){
        try {
            // Pobieranie danych liczbowych z pól tekstowych
            int mapHeight = Integer.parseInt(mapHeightField.getText().trim());
            int mapWidth = Integer.parseInt(mapWidthField.getText().trim());
            int plantEnergyProfit = Integer.parseInt(plantEnergyField.getText().trim());
            int minCopulationEnergy = Integer.parseInt(copulationEnergyField.getText().trim());
            int initialAnimalEnergy = Integer.parseInt(initialAnimalEnergyField.getText().trim());
            int energyCostPerMove = Integer.parseInt(energyCostPerMoveField.getText().trim());
            int initialAnimalsOnMap = Integer.parseInt(initialAnimalsField.getText().trim());
            int dailyPlantSpawns = Integer.parseInt(dailyPlantSpawnsField.getText().trim());
            int simSteps = Integer.parseInt(simulationSteps.getText().trim());
            int geneNum = Integer.parseInt(geneNumber.getText().trim());
            double mutationChance = Double.parseDouble(geneMutationChance.getText().trim());
            int waitingTime = Integer.parseInt(waitingTimeBetweenMoves.getText().trim());
            int initialPlantNumber = Integer.parseInt(initialPlantNumberField.getText().trim());

            // Pobieranie wartości boolean z checkboxów
            boolean fullPredestinationFlagValue = fullPredestinationFlag.isSelected();
            boolean oldnessSadnessFlagValue = oldnessSadnessFlag.isSelected();
            boolean equatorFlagValue = equatorFlag.isSelected();
            boolean liveGivingCorpseFlagValue = liveGivingCorpseFlag.isSelected();

            // Tworzenie obiektu SimulationParams
            return new SimulationParams(
                    mapHeight, mapWidth,
                    plantEnergyProfit, minCopulationEnergy, initialAnimalEnergy,
                    energyCostPerMove, initialAnimalsOnMap, dailyPlantSpawns, fullPredestinationFlagValue,
                    oldnessSadnessFlagValue, equatorFlagValue, liveGivingCorpseFlagValue,simSteps,
                    mutationChance, geneNum, waitingTime, initialPlantNumber
            );
        } catch (NumberFormatException e) {
            System.err.println("Invalid parameters! Please provide valid numeric values.");
        }
        return null;
    }

    WorldMap selectSimulationMap() throws Exception {
        if (params.fullPredestinationFlag() &&  params.equatorFlag()) return new FullPredestinationEquatorMap(params);
        if (params.fullPredestinationFlag() && params.liveGivingCorpseFlag()) return new FullPredestinationLiveGivingCorpseMap(params);
        if ( params.oldnessSadnessFlag() &&  params.equatorFlag())  return new OldnessSadnessEquatorMap(params);
        if ( params.oldnessSadnessFlag() && params.liveGivingCorpseFlag()) return new OldnessSadnessLiveGivingCorpseMap(params);

        throw new Exception("No Map Selected!");
    }
}























