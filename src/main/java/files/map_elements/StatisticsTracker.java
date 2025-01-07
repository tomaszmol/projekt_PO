package files.map_elements;

import javafx.scene.chart.XYChart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsTracker {

    private final Map<String, List<Number>> data;

    public StatisticsTracker() { this.data = new HashMap<>(); }

    public void addSeries(String seriesName) {
        data.putIfAbsent(seriesName, new ArrayList<>());
    }

    public void recordValue(String seriesName, Number value) {
        if (data.get(seriesName) == null) {
            throw new IllegalArgumentException("Series " + seriesName + " does not exist. Add it first using addSeries().");
        }
        System.out.println("adding to " + seriesName + ": " + value);
        System.out.println("b4 " + data.get(seriesName));
        data.get(seriesName).add(value);
        System.out.println("after " + data.get(seriesName));

    }

    public List<Number> getData(String seriesName) {
        System.out.println("getting data from " + seriesName + ": " + data.get(seriesName));
        return data.get(seriesName);
    }

    public void clearData() {
        data.clear();
    }
}
