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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.List;
import java.util.Objects;

public class SimulationPresenter implements MapChangeListener, DataAddedListener {

    private int updateCount = 0;
    SimulationParams params;
    boolean simulationPaused;
    private SimulationStats simulationStats;
    Animal selectedAnimal = null;

    @FXML
    public Button exportCSVButton;
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
    public Button pauseButton;

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
    private Simulation simulation;

    Image[] numberImg = new Image[] {
        new Image(Objects.requireNonNull(getClass().getResource("/numbers/num2.png")).toExternalForm()),
        new Image(Objects.requireNonNull(getClass().getResource("/numbers/num3.png")).toExternalForm()),
        new Image(Objects.requireNonNull(getClass().getResource("/numbers/num4.png")).toExternalForm()),
        new Image(Objects.requireNonNull(getClass().getResource("/numbers/num5.png")).toExternalForm()),
        new Image(Objects.requireNonNull(getClass().getResource("/numbers/num6.png")).toExternalForm()),
        new Image(Objects.requireNonNull(getClass().getResource("/numbers/num7.png")).toExternalForm()),
        new Image(Objects.requireNonNull(getClass().getResource("/numbers/num8.png")).toExternalForm()),
        new Image(Objects.requireNonNull(getClass().getResource("/numbers/num9.png")).toExternalForm()),
        new Image(Objects.requireNonNull(getClass().getResource("/numbers/numOver9.png")).toExternalForm())
    };

    Rectangle createGUIRectForWorldElement(WorldElement e, int cellSize) {
        return createGUIRect(e.getElementColour(), e.getElementSizeMultiplier(), HPos.CENTER, cellSize, cellSize);
    }
    Rectangle createGUIRect(Color colour, double sizeMult, HPos alignment, int width, int height) {
        Rectangle rect = new Rectangle(width*sizeMult, height*sizeMult);
        rect.setFill(colour);
        GridPane.setHalignment(rect, alignment);
        return rect;
    }
    ImageView createGUIImageForWorldElement(WorldElement e, int cellSize) {
        ImageView iv = createGUIImage(e.getImage(),e.getElementSizeMultiplier(),HPos.CENTER,cellSize);
        if (e instanceof Animal) {
            iv.setOnMouseClicked(event -> {selectedAnimal = (Animal)e; showAnimalInfo(selectedAnimal);});
        }
        return iv;
    }
    ImageView createGUIImage(Image img, double sizeMult, HPos alignment, int cellSize) {
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(cellSize*sizeMult); // Szerokość komórki
        imageView.setFitHeight(cellSize*sizeMult); // Wysokość komórki
        GridPane.setHalignment(imageView, alignment);
        return imageView;
    }
    void drawGrid (Vector2d lowerLeft, Vector2d upperRight, int cellSize) {
        int rows = upperRight.getY() - lowerLeft.getY() + 1;
        int columns = upperRight.getX() - lowerLeft.getX() + 1;

        for (int i = 0; i < columns; i++) {
            mapGrid.getColumnConstraints().add(new ColumnConstraints(cellSize));
        }
        for (int i = 0; i < rows; i++) {
            mapGrid.getRowConstraints().add(new RowConstraints(cellSize));
        }
    }
    void drawElements (Vector2d lowerLeft, Vector2d upperRight, int cellSize) {
        List<WorldElement> elements = simulationMap.getElements();

        for (WorldElement e : elements) {
            int x = e.getPosition().getX() - lowerLeft.getX();
            int y = upperRight.getY() - e.getPosition().getY();
            boolean isSelected = false;

            if (e.getClass() == Animal.class) {
                List<Animal> animals = simulationMap.getAnimals(e.getPosition());
                isSelected = e == selectedAnimal;
                if( isSelected ){
                    mapGrid.add( createGUIRect(Color.BLUE,0.99,HPos.CENTER,cellSize,cellSize),x,y);
                }
                if (animals.size()>=2)
                    mapGrid.add(createGUIImage(numberImg[Math.min(animals.size()-2,8)], .5, HPos.RIGHT, cellSize),x,y);

                // create energy display
                int maxEnergyDisplay = 300;
                double sizeMultiplier = (double) Math.min(maxEnergyDisplay, ((Animal) e).getEnergy()) / maxEnergyDisplay;
                System.out.println(sizeMultiplier +", "+ ((Animal) e).getEnergy() +", "+ getEnergyColour(sizeMultiplier));
                mapGrid.add( createGUIRect(getEnergyColour(sizeMultiplier),1,HPos.LEFT,cellSize/8,(int)(cellSize*Math.max(0.3,sizeMultiplier))),x, y);
            }
            if (e.hasImage()) {
                mapGrid.add( createGUIImageForWorldElement(e,cellSize),x,y);
            } else if(!isSelected) {
                mapGrid.add( createGUIRectForWorldElement(e,cellSize),x,y);
            }
        }
    }
    private void clearGrid() {
        mapGrid.getChildren().retainAll(mapGrid.getChildren().getFirst()); // Zachowaj linie siatki
        mapGrid.getColumnConstraints().clear();
        mapGrid.getRowConstraints().clear();
    }
    int calculateCellSize() {
        // Pobierz szerokość i wysokość okna
        double availableWidth = mapGrid.getScene().getWidth();
        double availableHeight = mapGrid.getScene().getHeight();

        // Pobierz rozmiar mapy z parametrów
        int mapWidth = params.mapWidth();
        int mapHeight = params.mapHeight();

        // Oblicz optymalny rozmiar komórki
        return (int) (Math.min(availableWidth / mapWidth, availableHeight / mapHeight) * 0.75);
    }


    public void drawMap() {
        Boundary b = simulationMap.getCurrentBounds();
        int cellSize = calculateCellSize();

        clearGrid();
        drawGrid(b.lowerLeft(), b.upperRight(), cellSize);
        drawElements(b.lowerLeft(), b.upperRight(), cellSize);
        showAnimalInfo(selectedAnimal);
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
        if (animal == null) return;
        animalInfoLabel.setText(animal.getAnimalInfo());
        if (animalInfoBoxRight != null) animalInfoBoxRight.setVisible(true);
    }

    void printGraph(StatisticsTracker tracker, LineChart<Number,Number> chart, String seriesName, int seriesNum){
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
//        Platform.runLater(() -> {
//            if (graph1Series.contains(seriesName) && populationChart != null)
//                printGraph(tracker, populationChart, seriesName, graph1Series.indexOf(seriesName));
//            if (graph2Series.contains(seriesName) && animalDataChart != null)
//                printGraph(tracker, animalDataChart, seriesName, graph2Series.indexOf(seriesName));
//            if (graph3Series.contains(seriesName) && geneticsChart != null)
//                printGraph(tracker, geneticsChart, seriesName, graph3Series.indexOf(seriesName));
//        });
    }

    public void setSimulationData(SimulationParams params, WorldMap map, StatisticsTracker tracker, Simulation sim) {
        this.params = params;

        this.simulation = sim;
        this.simulationMap = map;
        simulationMap.addObserver(this);


        this.statsTracker = tracker;
        for (String s : graph1Series) statsTracker.addSeries(s);
        for (String s : graph2Series) statsTracker.addSeries(s);
        for (String s : graph3Series) statsTracker.addSeries(s);


        this.simulationStats = sim.getSimulationStats(); // tutaj dodaj sobie obserwatora i jakoś zaimplementuj wyświetlanie tych statystyk

        statsTracker.addObserver(this);
    }

    public void onSimulationPaused() {
        simulationPaused = !simulationPaused;
        pauseButton.setText(simulationPaused ? "Resume" : "Pause");
        simulation.pause(simulationPaused);
        exportCSVButton.setText("Export Data");
    }

    public void onExportData(ActionEvent actionEvent) {
        if (!simulationPaused) onSimulationPaused();
        statsTracker.exportAllDataToCsv("simulationStatistics.csv");
        exportCSVButton.setText("Exported!");
    }

    //util lerp colour
    static Color[] energyColours = {
            Color.DARKRED,
            Color.RED,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.GREEN,
            Color.LIMEGREEN,
            Color.LIME
    };
    public static Color getEnergyColour(double t) {
        // Clamp t to the range [0, 1]
        t = Math.max(0, Math.min(1, t));
        return energyColours[(int) (t * (energyColours.length-1))];
    }
}























