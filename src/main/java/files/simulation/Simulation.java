package files.simulation;

import files.map_elements.Animal;
import files.map_elements.StatisticsTracker;
import files.maps.WorldMap;
import files.util.*;

import java.util.ArrayList;
import java.util.List;

// KOMENTARZ DO WYBRANEGO RODZAJU LISTY:
// do realizacji zadania najlepiej użyć listy ArrayList, ponieważ liczba elementów nie zmienia się w czasie działania programu - szybki dostęp do elementów jest ważniejszy niż usuwanie i dodawanie elementów.

public class Simulation implements Runnable {

    private List<Animal> animals;
    private final WorldMap map;
    final SimulationParams params;
    StatisticsTracker statisticsTracker;
    private boolean paused;
    private int day;
    private SimulationStats simulationStats;

    public Simulation(SimulationParams params, WorldMap map, StatisticsTracker statisticsTracker) throws Exception{
        this.map = map;
        this.animals = new ArrayList<>();
        this.params = params;
        this.statisticsTracker = statisticsTracker;
        spawnInitialAnimals();

    }

    private void spawnInitialAnimals() throws Exception{
        int animalNum = params.initialAnimalsOnMap();
        Boundary mapBoundaries = map.getCurrentBounds();
        System.out.println(mapBoundaries);
        for (int i=0; i<animalNum; i++) {
            Vector2d randPos;
            do { randPos = Vector2d.randomInBounds(mapBoundaries);
            } while (map.isOccupied(randPos));
            Animal animal = new Animal(randPos, params.geneNumber(), params.initialAnimalEnergy());
            map.placeAnimal(animal);
            putAnimalIntoSimulation(animal);
            animal.setDayOfBirth(0);
        }
    }

    public void run() {

        System.out.println("All objects on map: " + map.getElements());
        wait(1000);
        int energySum = 0;
        day = 0;

        for (day=0; day<params.simulationSteps(); day++) {
            //usunięcie martwych zwierzaków
            simulationRemoveDeadAnimals(day);

            //poruszanie się zwierzakow
            energySum = simulationMoveAllAnimals();

            simulationEatPlants();

            statisticsTracker.recordValue("Num animals", this.animals.size()); // to odpowiada za wszystkie zwierzaki na mapie, wraz z tymi, ktore juz umarły
            statisticsTracker.recordValue("Num plants", map.getPlants().size()); // liczba wszystkich roslin ktore obecnie sa na mapie
            statisticsTracker.recordValue("Avg energy", energySum/this.animals.size()); // to jest średnia energia, ktora przypada na wszystkie zwierzaki, ktore istnialy

            wait(300);

            //wzrost roslin
            simulationRegrowPlants();

            incrementAnimalsSurvivedDays();

        }

    }


    private void wait(int ms){
        try {
            Thread.sleep(ms);
            while (paused) Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void incrementAnimalsSurvivedDays() {
        List<Animal> listedAnimals = map.getAllAnimalsListed();
        for (Animal a : listedAnimals) {
            a.incrementSurvivedDays();
        }
    }

    private void simulationEatPlants() {
        map.eatPlants(params.plantEnergyProfit());
    }



    private void updateStats(){

    }

    public int simulationMoveAllAnimals() {
        int waitingTime = params.waitingTimeBetweenMoves();
        List<Animal> listedAnimals = map.getAllAnimalsListed();
        int energySum = 0;
        for (Animal a : listedAnimals) {
            map.moveAnimal(a);
            energySum += a.getEnergy();
            wait(waitingTime);
        }
        return energySum;
    }

    public void putAnimalIntoSimulation(Animal animal) {
        animals.add(animal);
    }

    public void pause(boolean state) {
        paused = state;
    }


    private void simulationRemoveDeadAnimals(int currentDay) {
        map.removeDeadAnimals(currentDay);
    }

    private void simulationRegrowPlants() {
        map.growPlants(params.dailyPlantSpawnNum());
    }

    public int getSimulationDay() {
        return day;
    }

    public SimulationStats getSimulationStats() {
        return simulationStats;
    }
}