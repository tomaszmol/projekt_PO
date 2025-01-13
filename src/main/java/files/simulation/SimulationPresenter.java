package files.simulation;

import files.map_elements.Animal;
import files.map_elements.StatisticsTracker;
import files.map_elements.WorldElement;
import files.maps.MapChangeListener;
import files.maps.WorldMap;
import files.util.Boundary;
import files.util.DataAddedListener;
import files.util.Vector2d;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import java.util.List;

public class SimulationPresenter implements MapChangeListener, DataAddedListener {

    private int updateCount = 0;
    SimulationParams params;

    @FXML
    public VBox animalInfoBoxRight;
    @FXML
    public Label animalInfoLabel;
    @FXML
    public Label dominantGene;
    @FXML
    private GridPane mapGrid;  // Powiązanie z kontrolką w FXML
    @FXML
    private Label moveDescriptionLabel;  // Powiązanie z kontrolką w FXML
    @FXML
    private Label updateCountLabel;  // Powiązanie z kontrolką w FXML

    @FXML
    public LineChart<Number, Number> populationChart;
    List<String> graph1Series = List.of("animals", "plants");

    @FXML
    public LineChart<Number, Number> animalDataChart;
    List<String> graph2Series = List.of( "energy" );

    @FXML
    public LineChart<Number, Number> geneticsChart;

    List<String> graph3Series =  List.of(  );
    private WorldMap simulationMap;
    private StatisticsTracker statsTracker;

    @FXML
    public void initialize() {
        if (animalInfoBoxRight != null) animalInfoBoxRight.setVisible(false);
    }

    public void drawMap() {
        // Czyszczenie siatki
        clearGrid();

        // nowa siatka
        int cellSize = 50;

        // nie dziala za bardzo
//        if (params != null) {
//            cellSize = (int) Math.min(
//                    mapGrid.getScene().getX() / params.mapWidth(),
//                    mapGrid.getScene().getY() / params.mapHeight()
//            );
//            System.out.println(mapGrid.getScene().getX() / params.mapWidth());
//            System.out.println(mapGrid.getScene().getY() / params.mapHeight());
//        }

        double cellContentSizeMultiplier = 0.8;

        // dane
        Boundary bounds = simulationMap.getCurrentBounds();
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
                WorldElement element = simulationMap.objectAt(position);

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
            moveDescriptionLabel.setText((message != null) ? message : "No message");
            updateCountLabel.setText("Update count: #" + updateCount);
        });
    }

    private void showAnimalInfo(Animal animal) {
        animalInfoLabel.setText(animal.getAnimalInfo());
        if (animalInfoBoxRight != null) animalInfoBoxRight.setVisible(true);
    }

    void printGraph(StatisticsTracker tracker, LineChart<Number,Number> chart, String seriesName, int seriesNum){
        System.out.println("Printing graph " + seriesName);

        // rozwiaz problem
        List<Number> data = tracker.getData(seriesName);

        // wprowadz nowe dane
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < data.size(); i++) {
            series.getData().add(new XYChart.Data<>(i, data.get(i)));
        }

        // dodaj nowe dane
        if (chart.getData().size() > seriesNum) chart.getData().set(seriesNum, series);
        else chart.getData().add(seriesNum, series);
    }

    @Override
    public void onDataAdded(StatisticsTracker tracker, String seriesName) {
        System.out.println("PRINTING GRAPHS");
        Platform.runLater(() -> {
            if (graph1Series.contains(seriesName) && populationChart != null)
                printGraph(tracker, populationChart, seriesName, graph1Series.indexOf(seriesName));
            if (graph2Series.contains(seriesName) && animalDataChart != null)
                printGraph(tracker, animalDataChart, seriesName, graph2Series.indexOf(seriesName));
            if (graph3Series.contains(seriesName) && geneticsChart != null)
                printGraph(tracker, geneticsChart, seriesName, graph3Series.indexOf(seriesName));
        });
    }

    public void setSimulationData(SimulationParams params, WorldMap map, StatisticsTracker tracker) {
        this.params = params;

        this.simulationMap = map;
        simulationMap.addObserver(this);

        this.statsTracker = tracker;
        for (String s : graph1Series) statsTracker.addSeries(s);
        for (String s : graph2Series) statsTracker.addSeries(s);
        for (String s : graph3Series) statsTracker.addSeries(s);
        statsTracker.addObserver(this);
    }
}























