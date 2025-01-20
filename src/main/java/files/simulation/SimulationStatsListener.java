package files.simulation;

import files.maps.WorldMap;

public interface SimulationStatsListener {
    void SimulationStatsChanged(SimulationStats simulationStats, String message);
}


