package files.simulation;

import files.map_elements.Animal;
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

    public Simulation(SimulationParams params, WorldMap map) throws Exception{

        this.map = map;
        animals = new ArrayList<Animal>();
        this.params = params;

        // spawn initial animals:
        int animalNum = params.initialAnimalsOnMap();
        Boundary mapBoundaries = map.getCurrentBounds();
        for (int i=0; i<animalNum; i++) {
            Vector2d randPos;
            do { randPos = Vector2d.randomInBounds(mapBoundaries);
            } while (map.isOccupied(randPos));
            Animal animal = new Animal(randPos);
            animals.add(animal);
        }
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    private void wait(int ms){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {

        System.out.println("All objects on map: " + map.getElements());
        wait(1000);

        for (int day=0; day<params.simulationSteps(); day++) {
            for (Animal a : animals) {
                map.move(a);
                wait(100);
                day += 1;
            }
            day -= 1;
            wait(500);
        }

    }
}