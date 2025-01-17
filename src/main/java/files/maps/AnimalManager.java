package files.maps;

import files.map_elements.Animal;
import files.map_elements.Plant;
import files.simulation.SimulationParams;
import files.util.MapDirection;
import files.util.Vector2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class AnimalManager extends AbstractEarthMap{

    public AnimalManager(SimulationParams params) {
        super(params);
    }


    public void moveAnimal(Animal animal) {


        // Pobierz listę zwierzaków na poprzedniej pozycji
        List<Animal> animalsOnPosition = animals.get(animal.getPosition());

        // Sprawdzenie, czy lista zwierzaków w tej pozycji istnieje
        if (animalsOnPosition != null) {
            // Sprawdzamy, czy zwierzak znajduje się w tej liście
            if (animalsOnPosition.contains(animal)) {
                // Zapisz starą pozycję i orientację
                Vector2d oldPos = animal.getPosition();
                MapDirection oldOrientation = animal.getOrientation();

                // Jeśli zwierzak się poruszył
                if (animal.move(this, params.energyCostPerMove())) {
                    // Usuń zwierzaka z poprzedniej pozycji (ze starej listy)
                    animalsOnPosition.remove(animal);

                    // Jeśli lista na danej pozycji jest teraz pusta, usuń ją z HashMap
                    if (animalsOnPosition.isEmpty()) {
                        animals.remove(oldPos);
                    }

                    // Zaktualizuj listę zwierzaków na nowej pozycji
                    List<Animal> newAnimalsOnPosition = animals.get(animal.getPosition());
                    if (newAnimalsOnPosition == null) {
                        newAnimalsOnPosition = new ArrayList<>(); // Inicjujemy nową listę, jeśli na tej pozycji nie ma jeszcze zwierzaków
                    }

                    newAnimalsOnPosition.add(animal); // Dodajemy zwierzaka na nową pozycję
                    animals.put(animal.getPosition(), newAnimalsOnPosition); // Aktualizujemy HashMap

                    // Powiadom obserwatorów
                    notifyObservers("Animal moved from " + oldPos + " to " + animal.getPosition());
                } else {
                    // Sprawdzenie, czy orientacja zmieniła się
                    MapDirection newOrientation = animal.getOrientation();
                    if (oldOrientation != newOrientation) {
                        notifyObservers("Animal changed orientation from " + oldOrientation + " to " + newOrientation);
                    } else {
                        notifyObservers("Animal did not move and did not change orientation");
                    }
                }
            } else {
                // Jeśli zwierzak nie jest na tej pozycji (index byłby nieprawidłowy)
                System.out.println("Błąd: nie ma zwierzaka na tej pozycji.");
            }
        } else {
            // Jeśli lista zwierzaków jest null (czyli brak zwierzaków na tej pozycji)
            System.out.println("Błąd: brak zwierzaków na tej pozycji.");
        }

    }


    public List<Animal> getAnimals (Vector2d position) {
        if (animals.containsKey(position)) {
            return animals.get(position);
        }
        return null;
    }


    public List<Vector2d> removeDeadAnimals() {
        final int energyCostPerMove = params.energyCostPerMove();
        List<Vector2d> emptyPositions = new ArrayList<>();

        for (Map.Entry<Vector2d, List<Animal>> entry : animals.entrySet()) {
            Vector2d position = entry.getKey();
            List<Animal> animalList = entry.getValue();

            // Usuwamy martwe zwierzęta z listy
            animalList.removeIf(animal -> animal.getEnergy() < energyCostPerMove);

            // Jeśli lista jest pusta, dodajemy jej pozycję do listy do usunięcia
            if (animalList.isEmpty()) {
                emptyPositions.add(position);
            }
        }

        // Usuwamy pozycje z mapy, gdzie listy były puste
        for (Vector2d position : emptyPositions) {
            animals.remove(position);
        }

        notifyObservers("Dead animals have been removed");

        return emptyPositions;
    }


    private Animal resolveConflict(Vector2d position, List<Animal> animalsAtPosition) {

        if (animalsAtPosition == null || animalsAtPosition.isEmpty()) {
            return null; // Brak zwierząt na pozycji, brak konfliktu
        }

        // Poszukiwanie zwierząt o najwyższej energii
        int maxEnergy = animalsAtPosition.stream()
                .mapToInt(Animal::getEnergy)
                .max()
                .orElse(0);

        List<Animal> strongestAnimals = animalsAtPosition.stream()
                .filter(animal -> animal.getEnergy() == maxEnergy)
                .toList();

        if (strongestAnimals.size() == 1) {
            return strongestAnimals.getFirst(); // Jedno zwierzę z największą energią
        }

        // Poszukiwanie najstarszych zwierząt wśród remisujących
        int maxAge = strongestAnimals.stream()
                .mapToInt(Animal::getSurvivedDays)
                .max()
                .orElse(0);

        List<Animal> oldestAnimals = strongestAnimals.stream()
                .filter(animal -> animal.getSurvivedDays() == maxAge)
                .toList();

        if (oldestAnimals.size() == 1) {
            return oldestAnimals.getFirst(); // Jedno najstarsze zwierzę
        }

        // Poszukiwanie zwierząt o największej liczbie dzieci wśród remisujących
        int maxChildren = oldestAnimals.stream()
                .mapToInt(Animal::getNumberOfChildren)
                .max()
                .orElse(0);

        List<Animal> mostProlificAnimals = oldestAnimals.stream()
                .filter(animal -> animal.getNumberOfChildren() == maxChildren)
                .toList();

        if (mostProlificAnimals.size() == 1) {
            return mostProlificAnimals.getFirst(); // Jedno zwierzę o największej liczbie dzieci
        }

        // Wybór losowy w przypadku całkowitego remisu
        Random random = new Random();
        return mostProlificAnimals.get(random.nextInt(mostProlificAnimals.size()));
    }

    public Animal resolveFoodConflict(Vector2d position){
        List<Animal> animalsAtPosition = getAnimals(position);
        if (animalsAtPosition == null || animalsAtPosition.isEmpty()) {
            return null; // Brak zwierząt na pozycji, brak konfliktu
        }
        return resolveConflict(position, animalsAtPosition);
    }

    public void eatPlants(int energyProfit) {
        // Iterujemy po wszystkich roślinach na mapie
        List<Vector2d> plantsToRemove = new ArrayList<>();
        for (Map.Entry<Vector2d, Plant> entry : plants.entrySet()) {
            Vector2d position = entry.getKey();
            Plant plant = entry.getValue();

            // Pobieramy listę zwierząt na tej samej pozycji
            List<Animal> animalsAtPosition = getAnimals(position);


            // Sprawdzamy, czy są zwierzęta na tej pozycji
            if (animalsAtPosition != null && !animalsAtPosition.isEmpty()) {
                if (animalsAtPosition.size() == 1) {
                    // Jeśli jest tylko jedno zwierzę na pozycji, to ono zjada roślinę
                    Animal animal = animalsAtPosition.getFirst();
                    animal.eatFood(energyProfit);// Zwierzę je roślinę
                    plantsToRemove.add(position);  // Usuwamy roślinę z mapy po jej zjedzeniu
                } else {
                    // Jeśli jest więcej niż jedno zwierzę, rozstrzygamy konflikt
                    Animal winner = resolveFoodConflict(position);
                    if (winner != null) {
                        winner.eatFood(energyProfit);  // Zwycięzca je roślinę
                        plantsToRemove.add(position); // Usuwamy roślinę z mapy po jej zjedzeniu
                    }
                }
            }

        }

        // Usuwamy zjedzone rośliny z mapy
        for (Vector2d position : plantsToRemove) {
            plants.remove(position);
        }

        notifyObservers("Plants have been eaten");
    }


    public void animalFight () {
        // walka
    }

    // funckje nie testowana w ogole :[
    public void animalCopulation (Animal mother, Animal father) {
        if (mother.getEnergy() < params.minCopulationEnergy() || father.getEnergy() < params.minCopulationEnergy()) {
            return;
        }

        Vector2d pos = mother.getPosition();
        Animal child = new Animal(pos,params.geneNumber());

        // inherit genes
        child.getGenetics().inheritGenesFromParents(
                mother.getGenetics(), mother.getEnergy(),
                father.getGenetics(), father.getEnergy()
        );

        // mutation
        child.getGenetics().mutateGeneticCode( params.geneMutationChance() );

        // receive parent energy
        mother.useEnergy(params.minCopulationEnergy());
        father.useEnergy(params.minCopulationEnergy());
        child.useEnergy(-2*params.minCopulationEnergy()); // po ci za duzo funkcji -\ :] /-

        // place on map
        placeAnimal(child);

        // update animal statistics
        mother.addChild();
        father.addChild();
    }

    public void placeAnimal(Animal animal) {

        // Zaktualizuj listę zwierzaków na nowej pozycji
        List<Animal> newAnimalsOnPosition = animals.get(animal.getPosition());
        if (newAnimalsOnPosition == null) {
            newAnimalsOnPosition = new ArrayList<>(); // Inicjujemy nową listę, jeśli na tej pozycji nie ma jeszcze zwierzaków
        }

        newAnimalsOnPosition.add(animal); // Dodajemy zwierzaka na nową pozycję
        animals.put(animal.getPosition(), newAnimalsOnPosition); // Aktualizujemy HashMap
        notifyObservers("Animal placed at " + animal.getPosition());
    }

    public List<Animal> getAllAnimalsListed() {
        List<Animal> elements = new ArrayList<>();

        for (List<Animal> animalList : animals.values()) {
            elements.addAll(animalList);  // Dodaj wszystkie zwierzaki do głównej listy
        }
        return elements;
    }
}
