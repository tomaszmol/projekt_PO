package files.simulation;

import files.map_elements.Genetics;
import files.maps.MapChangeListener;
import files.maps.WorldMap;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimulationStats implements SimulationStatsListener {
    protected HashMap<Genetics, Integer> mostPopularGenetics;
    protected int mostPopularGeneticsCount;
    protected Genetics mostPopularGenetic; //najpopularniejszych genotypów,
    protected double averageEnergyOfLivingAnimals; //średniego poziomu energii dla żyjących zwierzaków,
    protected double averageLifeLengthOfDeadAnimals; // średniej długości życia zwierzaków dla martwych zwierzaków (wartość uwzględnia wszystkie nieżyjące zwierzaki - od początku symulacji),
    protected double averageNumberOfChildren;// średniej liczby dzieci dla żyjących zwierzaków (wartość uwzględnia wszystkie powstałe zwierzaki, a nie tylko zwierzaki powstałe w danej epoce).
    protected int day;
    protected int energySum;
    protected int numberOfLivingAnimals;//liczby wszystkich zwierzaków,
    protected int numberOfPlants;//liczby wszystkich roślin,
    protected int numberOfEmptyFields; //liczby wolnych pól,
    protected int childrenSum; //liczby wszystkich dzieci,
    protected int numberOfDeathAnimals; //liczby wszystkich martwych zwierzaków.
    protected int sumNumberOfSurvivedDays;

    private final List<SimulationStatsListener> observers = new ArrayList<>();//sumy dni przeżytych przez wszystkie zwierzaki,

    public SimulationStats(){
        this.mostPopularGeneticsCount = 0;
        this.averageEnergyOfLivingAnimals = 0;
        this.averageLifeLengthOfDeadAnimals = 0;
        this.averageNumberOfChildren = 0;
        this.day = 0;
        this.energySum = 0;
        this.numberOfLivingAnimals = 0;
        this.numberOfPlants = 0;
        this.numberOfEmptyFields = 0;
        this.childrenSum = 0;
        this.numberOfDeathAnimals = 0;
        this.sumNumberOfSurvivedDays = 0;
    }

    @Override
    public void SimulationStatsChanged(SimulationStats simulationStats, String message) {

    }

    public void addObserver(SimulationStatsListener observer) {
        observers.add(observer);
    }

    public void removeObserver(SimulationStatsListener observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (SimulationStatsListener observer : observers) {
            observer.SimulationStatsChanged(this, message);
        }
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");
        String result = String.format("Day: #%d\n" +
                        "Number of living animals: %d\n" +
                        "Number of plants: %d\n" +
                        "Number of empty fields: %d\n" +
                        "Most popular genetics: %s (*%d)\n" +
                        "Average energy of living animals: %.2f\n" +
                        "Average life length of dead animals: ",
                day, numberOfLivingAnimals, numberOfPlants, numberOfEmptyFields, mostPopularGenetic, mostPopularGeneticsCount, averageEnergyOfLivingAnimals);

        if (numberOfDeathAnimals > 0) {
            result += String.format("%.2f\n", averageLifeLengthOfDeadAnimals);
        } else {
            result += "all alive\n";
        }

        result += String.format("Average number of children: %.2f\n", averageNumberOfChildren);


        return result;
    }
}