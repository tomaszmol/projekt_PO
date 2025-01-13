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

    private final List<Animal> animals;
    private final WorldMap map;
    final SimulationParams params;
    StatisticsTracker stats;
    private boolean paused;

    public Simulation(SimulationParams params, WorldMap map, StatisticsTracker stats) throws Exception{
        this.map = map;
        animals = new ArrayList<>();
        this.params = params;
        this.stats = stats;

        // spawn initial animals:
        int animalNum = params.initialAnimalsOnMap();
        Boundary mapBoundaries = map.getCurrentBounds();
        System.out.println(mapBoundaries);
        for (int i=0; i<animalNum; i++) {
            Vector2d randPos;
            do { randPos = Vector2d.randomInBounds(mapBoundaries);
            } while (map.isOccupied(randPos));
            Animal animal = new Animal(randPos, params.geneNumber());
            map.placeAnimal(animal);
            addAnimalToSimulation(animal);
        }
    }

    public List<Animal> getAnimals() {
        return animals;
    }
    public void addAnimalToSimulation(Animal animal) { animals.add(animal); }

    private void wait(int ms){
        try {
            Thread.sleep(ms);
            while (paused) Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {

        System.out.println("All objects on map: " + map.getElements());
        wait(1000);

        for (int day=0; day<params.simulationSteps(); day++) {
            int energySum = 0;

            for (Animal a : animals) {
                map.move(a);
                energySum += a.getEnergy();
                wait(100);
            }

            map.resolveConflicts();
            map.removeDeadAnimals();

            stats.recordValue("animals", animals.size());
            stats.recordValue("plants", map.getPlants().size());
            stats.recordValue("energy", energySum/animals.size());

            wait(300);
        }

    }

    public void pause(boolean state) {
        paused = state;
    }
}