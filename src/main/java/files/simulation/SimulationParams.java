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
        boolean fullPredestinationFlag,
        boolean oldnessSadnessFlag,
        boolean equatorFlag,
        boolean liveGivingCorpseFlag,
        int simulationSteps,
        double geneMutationChance,
        int geneNumber
) {}
