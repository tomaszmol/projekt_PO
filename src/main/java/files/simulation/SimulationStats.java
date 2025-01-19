package files.simulation;

import files.map_elements.Genetics;

public record SimulationStats(
        int livingAnimalsNumber, //liczby wszystkich zwierzaków,
        int plantNumber,//liczby wszystkich roślin,
        int emptyFieldsNumber, //liczby wolnych pól,
        Genetics mostPopularGenetics, //najpopularniejszych genotypów,
        double averageEnergyOfLivingAnimals, //średniego poziomu energii dla żyjących zwierzaków,
        double averageLifeLengthOfDeadAnimals, // średniej długości życia zwierzaków dla martwych zwierzaków (wartość uwzględnia wszystkie nieżyjące zwierzaki - od początku symulacji),
        double averageNumberOfChildren // średniej liczby dzieci dla żyjących zwierzaków (wartość uwzględnia wszystkie powstałe zwierzaki, a nie tylko zwierzaki powstałe w danej epoce).
) {
}