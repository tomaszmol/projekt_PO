package files.simulation;

public record SimulationParams(
        int mapHeight,
        int mapWidth,
//        int jungleHeight,
//        int jungleWidth,
        int plantEnergyProfit,
        int minCopulationEnergy,
        int initialAnimalEnergy,
        int dailyAnimalEnergyCost,
        int initialAnimalsOnMap,
        int dailyPlantSpawnNum,
        boolean ageFlag,
        boolean circleOfLifeFlag,
        boolean earthMapFlag,
        int simulationSteps
) {}
