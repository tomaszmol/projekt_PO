package files.simulation;

import files.map_elements.Animal;
import files.map_elements.Genetics;
import files.map_elements.StatisticsTracker;
import files.maps.WorldMap;
import files.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// KOMENTARZ DO WYBRANEGO RODZAJU LISTY:
// do realizacji zadania najlepiej użyć listy ArrayList, ponieważ liczba elementów nie zmienia się w czasie działania programu - szybki dostęp do elementów jest ważniejszy niż usuwanie i dodawanie elementów.

public class Simulation extends SimulationStats implements Runnable  {

    private List<Animal> animals;
    private final WorldMap map;
    final SimulationParams params;
    StatisticsTracker statisticsTracker;
    private boolean paused;
    private SimulationStats simulationStats;

    public Simulation(SimulationParams params, WorldMap map, StatisticsTracker statisticsTracker) throws Exception{
        this.map = map;
        this.animals = new ArrayList<>();
        this.params = params;
        this.statisticsTracker = statisticsTracker;
        spawnInitialAnimals();
        notifyObservers(String.format("Day: \n" +
                        "Number of living animals: \n" +
                        "Number of plants: \n" +
                        "Number of empty fields: \n" +
                        "Most popular genetics: \n" +
                        "Average energy of living animals: \n" +
                        "Average life length of dead animals: \n"+
                        "Average number of children: \n"));

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
            map.notifyObservers("Animal placed at " + animal.getPosition());
            putAnimalIntoSimulation(animal);
            animal.setDayOfBirth(0);
        }
    }

    public void run() {

        System.out.println("All objects on map: " + map.getElements());
        wait(1000);
        energySum = 0;

        for (day=0; day<params.simulationSteps(); day++) {
            //usunięcie martwych zwierzaków
            simulationRemoveDeadAnimals();

            //poruszanie się zwierzakow
            simulationMoveAllAnimals();

            simulationEatPlants();


            simulationReproduceAnimals();


            updateAllStats();
            statisticsTracker.recordValue("Num animals", numberOfLivingAnimals); // to odpowiada za wszystkie zwierzaki w symulacji, wraz z tymi, ktore juz umarły
            statisticsTracker.recordValue("Num plants", numberOfPlants);// liczba wszystkich roslin ktore obecnie sa na mapie
            statisticsTracker.recordValue("Avg energy", averageEnergyOfLivingAnimals); // to jest średnia energia, ktora przypada na wszystkie zwierzaki, ktore istnieja
            //wzrost roslin
            simulationRegrowPlants();

            incrementAnimalsSurvivedDays();


            wait(300);

        }

    }

    private void simulationReproduceAnimals() {
        List <Animal> toAdd = map.animalsReproduce();
        for (Animal a : toAdd) {
            if (toAdd!=null)
//                System.out.println("toAdd: " + a.getPosition());
            if (a != null){
                putAnimalIntoSimulation(a);
                map.placeAnimal(a);
                a.setDayOfBirth(day);
                map.notifyObservers("Animal born at " + a.getPosition());
            }

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
            a.setSurvivedDays(a.getSurvivedDays() + 1);
        }
    }

    private void simulationEatPlants() {
        map.eatPlants(params.plantEnergyProfit());
    }



    private void updateStats(){

    }

    public void simulationMoveAllAnimals() {
        int waitingTime = params.waitingTimeBetweenMoves();
        List<Animal> listedAnimals = map.getAllAnimalsListed();
        for (Animal a : listedAnimals) {
            map.moveAnimal(a);
            energySum += a.getEnergy();
            wait(waitingTime);
        }
    }

    public void putAnimalIntoSimulation(Animal animal) {
        if (animal != null) {
            animals.add(animal);
        }
        else {
            System.out.println("Animal is null");
        }

    }

    public void pause(boolean state) {
        paused = state;
    }


    private void simulationRemoveDeadAnimals() {
        map.removeDeadAnimals(day);
    }

    private void simulationRegrowPlants() {
        map.growPlants(params.dailyPlantSpawnNum());
    }

    public int getSimulationDay() {
        return day;
    }

    public SimulationStats getSimulationStatsToString() {
        return simulationStats;
    }

    public void updateAllStats() {
        updateEnergySum();
        updateNumberOfLivingAnimals();
        updateNumberOfPlants();
        updateAverageEnergyOfLivingAnimals();
        childrenSum();
        updateAverageNumberOfChildren();
        updateNumberOfEmptyFields();
        updateNumberOfDeathAnimalsAndSurvivedDays();
        updateAverageLifeLengthOfDeadAnimals();
        updateMostPopularGenetics();
        notifyObservers(this.toString());
    }

    public void updateEnergySum() {
        energySum = 0;
        for (Animal a : map.getAllAnimalsListed()) {
            energySum += a.getEnergy();
        }
    }

    public void updateNumberOfLivingAnimals() {
        numberOfLivingAnimals = map.getAllAnimalsListed().size();
    }
    public void updateNumberOfPlants() {
        numberOfPlants = map.getPlants().size();
    }
    public void updateAverageEnergyOfLivingAnimals() {
        averageEnergyOfLivingAnimals = (double) energySum /numberOfLivingAnimals;
    }

    public void childrenSum() {
        childrenSum = 0;
        for (Animal a : map.getAllAnimalsListed()) {
            childrenSum += a.getNumberOfChildren();
        }
    }

    public void updateAverageNumberOfChildren() {
        averageNumberOfChildren = (double) childrenSum / numberOfLivingAnimals;
    }

    public void updateNumberOfEmptyFields() {
        numberOfEmptyFields = 0;
        Boundary bounds = map.getCurrentBounds();
        for (int x = bounds.lowerLeft().getX(); x <= bounds.upperRight().getX(); x++) {
            for (int y = bounds.lowerLeft().getY(); y <= bounds.upperRight().getY(); y++) {
                Vector2d position = new Vector2d(x, y);
                if (!map.isOccupied(position) && map.objectAt(position) == null) {
                    numberOfEmptyFields++;
                }
            }
        }
    }

    public void updateNumberOfDeathAnimalsAndSurvivedDays() {
        numberOfDeathAnimals = 0;
        sumNumberOfSurvivedDays = 0;
        for (Animal a : animals) {
            if (a.getDayOfDeath() != -1) {
                numberOfDeathAnimals++;
                sumNumberOfSurvivedDays += a.getSurvivedDays();
            }
        }
    }

    public void updateAverageLifeLengthOfDeadAnimals() {
        averageLifeLengthOfDeadAnimals = (double) sumNumberOfSurvivedDays / numberOfDeathAnimals;
    }

    public void updateMostPopularGenetics() {

        mostPopularGenetics = new HashMap<Genetics, Integer>();
        mostPopularGenetic = null;
        mostPopularGeneticsCount = 0;
        for (Animal a : animals) {
            Genetics genetics = a.getGenetics();
            if (mostPopularGenetics.containsKey(genetics)) {
                int newCount = mostPopularGenetics.get(genetics) + 1;
                mostPopularGenetics.put(genetics, newCount);
            } else {
                mostPopularGenetics.put(genetics, 1);
            }
            if (mostPopularGenetics.get(genetics) > mostPopularGeneticsCount) {
                mostPopularGeneticsCount = mostPopularGenetics.get(genetics);
                mostPopularGenetic = genetics;
            }
        }
    }
}