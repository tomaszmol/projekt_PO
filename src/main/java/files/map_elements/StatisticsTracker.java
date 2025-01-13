package files.map_elements;

import files.util.DataAddedListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsTracker {

    private final Map<String, List<Number>> data;
    private final List<DataAddedListener> observers = new ArrayList<>();

    public StatisticsTracker() {
        this.data = new HashMap<>();
    }

    public void addSeries(String seriesName) {
        data.putIfAbsent(seriesName, new ArrayList<>());
    }

    public void recordValue(String seriesName, Number value) {
        if (data.get(seriesName) == null) {
            throw new IllegalArgumentException("Series " + seriesName + " does not exist. Add it first using addSeries().");
        }
        data.get(seriesName).add(value);
        notifyObservers(seriesName);
    }

    public List<Number> getData(String seriesName) {
        return data.get(seriesName);
    }

    public void clearData() {
        data.clear();
    }

    public void addObserver(DataAddedListener observer) {
        observers.add(observer);
    }

    public void removeObserver(DataAddedListener observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String seriesName) {
        for (DataAddedListener observer : observers) {
            observer.onDataAdded(this, seriesName);
        }
    }

    public void exportAllDataToCsv(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Map.Entry<String, List<Number>> entry : data.entrySet()) {
                String seriesName = entry.getKey();
                List<Number> values = entry.getValue();
                writer.append(seriesName).append(";");
                for (int i = 0; i < values.size(); i++) {
                    writer.append(values.get(i).toString());
                    if (i < values.size() - 1) {
                        writer.append(";");
                    }
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error while exporting data to file " + filePath);
        }
    }
}
