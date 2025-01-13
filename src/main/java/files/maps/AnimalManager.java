package files.maps;

import files.map_elements.Animal;
import files.simulation.SimulationParams;
import files.util.MapDirection;
import files.util.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class AnimalManager extends AbstractEarthMap{

    public AnimalManager(SimulationParams params) {
        super(params);
    }

    public void move(Animal animal) {
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
                if (animal.move(this)) {
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

    public void resolveConflicts(){
        // SMTH
    }

    public void removeDeadAnimals(){
        // SMTH
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
}
