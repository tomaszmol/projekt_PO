package files.util;

import files.map_elements.StatisticsTracker;

public interface DataAddedListener {
    void onDataAdded (StatisticsTracker tracker, String seriesName);
}
