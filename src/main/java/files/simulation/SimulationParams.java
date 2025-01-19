package files.simulation;

public record SimulationParams(
        int mapHeight,
        int mapWidth,
//        int jungleHeight,
//        int jungleWidth,
        int plantEnergyProfit,
        int minCopulationEnergy,
        int initialAnimalEnergy,
        int energyCostPerMove,
        int initialAnimalsOnMap,
        int dailyPlantSpawnNum,
        boolean fullPredestinationFlag,
        boolean oldnessSadnessFlag,
        boolean equatorFlag,
        boolean liveGivingCorpseFlag,
        int simulationSteps,
        int geneNumber,
        int waitingTimeBetweenMoves,
        int initialPlantNumber,
        int copulationEnergyUse,
        int minMutationNum,
        int maxMutationNum
) {}
