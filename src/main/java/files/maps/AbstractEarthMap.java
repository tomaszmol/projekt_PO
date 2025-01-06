package files.maps;

import files.map_elements.*;
import files.util.*;

import java.util.*;

public class AbstractEarthMap implements WorldMap {

    protected HashMap<Vector2d, List<Animal>> animals; //przechowywanie zwierzakow jako listy zwierzakow na danej pozycji, poniewaz moze byc kilka zwierzakow na jedynm polu
    protected HashMap<Vector2d, Plant> plants;
    protected HashMap<Vector2d, PreferredField> preferredFields;// roslina jest tylko jedna na danej pozycji
    private final List<MapChangeListener> observers = new ArrayList<>();
    private final UUID id;
    private final Vector2d mapDimensions;

    public AbstractEarthMap(int mapWidth, int mapHeight) {
        this.mapDimensions = new Vector2d(mapWidth - 1, mapHeight - 1);
        this.animals = new HashMap<>();
        this.plants = new HashMap<>();
        this.preferredFields = new HashMap<>();
        this.id = UUID.randomUUID();
    }

    public void addObserver(MapChangeListener observer) {
        observers.add(observer);
    }

    public void removeObserver(MapChangeListener observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this, message);
        }
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


    public void placeAnimal(Animal animal) throws Exception {

        // Zaktualizuj listę zwierzaków na nowej pozycji
        List<Animal> newAnimalsOnPosition = animals.get(animal.getPosition());
        if (newAnimalsOnPosition == null) {
            newAnimalsOnPosition = new ArrayList<>(); // Inicjujemy nową listę, jeśli na tej pozycji nie ma jeszcze zwierzaków
        }

        newAnimalsOnPosition.add(animal); // Dodajemy zwierzaka na nową pozycję
        animals.put(animal.getPosition(), newAnimalsOnPosition); // Aktualizujemy HashMap
        notifyObservers("Animal placed at " + animal.getPosition());
    }

    public void placePlant(Plant plant) {
        if (plant == null || plant.getPosition() == null) {
            // Ignorujemy, jeżeli roślina lub jej pozycja są null
            return;
        }

        // Sprawdzenie, czy na danej pozycji już jest roślina
        if (plants.containsKey(plant.getPosition())) {
            // Jeśli roślina już istnieje na tej pozycji, po prostu ignorujemy próbę jej umieszczenia
            return;
        }

        // Umieszczamy roślinę, jeśli nie ma jej już na danej pozycji
        plants.put(plant.getPosition(), plant);

        // Aktualizacja stanu preferowanego pola, jeśli istnieje
        PreferredField preferredField = preferredFields.get(plant.getPosition());
        if (preferredField != null) {
            preferredField.setPlantGrown(true);
        }
    }

    public void placePreferredField(PreferredField preferredField) {
        if (preferredField == null || preferredField.getPosition() == null) {
            // Ignorujemy, jeżeli pole lub jego pozycja są null
            return;
        }

        // Sprawdzenie, czy na danej pozycji już jest preferowane pole
        if (preferredFields.containsKey(preferredField.getPosition())) {
            // Jeśli preferowane pole już istnieje na tej pozycji, po prostu ignorujemy próbę jego umieszczenia
            return;
        }

        // Umieszczamy preferowane pole, jeśli nie ma go już na danej pozycji
        preferredFields.put(preferredField.getPosition(), preferredField);
    }



    public boolean isOccupied(Vector2d position) {
        return animals.get(position) != null;
    }

    public WorldElement objectAt(Vector2d position) {
        WorldElement element = null;
        if (preferredFields.containsKey(position)) {
            element = preferredFields.get(position);
        }
        if (plants.containsKey(position)) {
            element = plants.get(position);
        }
        if (animals.containsKey(position)) {
            List<Animal> animalsOnPosition = animals.get(position);
            if (animalsOnPosition != null) {
                element = animalsOnPosition.getLast();
            }
        }
        return element;
    }

    public List<WorldElement> getElements() {
        List<WorldElement> elements = new ArrayList<>();
        // Iteruj po każdej liście zwierzaków w mapie
        for (List<Animal> animalList : animals.values()) {
            elements.addAll(animalList);  // Dodaj wszystkie zwierzaki do głównej listy
        }
        if (plants != null) {
            elements.addAll(plants.values());
        }
        if (preferredFields != null) {
            elements.addAll(preferredFields.values());
        }
        return elements;
    }

    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), mapDimensions);
    }

    public boolean canMoveTo(Vector2d position) {
        return true;
    }


    public PositionAndOrientation positionAndOrientationAfterMovement(Vector2d origin, MapDirection currentOrientation, MoveDirection direction){
        Vector2d shift = currentOrientation.toUnitVector();
        if (direction == MoveDirection.BACKWARD) shift = shift.multiplyByScalar(-1);
        return new PositionAndOrientation(origin.add(shift),currentOrientation);
    }

    public String toString() {
        Boundary bounds = getCurrentBounds();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();
        return new MapVisualizer(this).draw(lowerLeft, upperRight);
    }

    public UUID getId() {
        return this.id;
    }

    public void growPlantsOnWholeMap() {
        Random random = new Random();

        // Iterujemy po każdej pozycji na mapie
        Boundary bounds = getCurrentBounds();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();


        for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
            for (int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
                Vector2d position = new Vector2d(x, y);

                // Sprawdzenie, czy pole jest preferowane
                boolean isPreferred = preferredFields.containsKey(position);

                // Losowanie zgodnie z zasadą Pareto
                double chance = isPreferred ? 0.8 : 0.2;
                if (random.nextDouble() < chance) {
                    // Dodaj roślinę, jeśli jej tam jeszcze nie ma
                    if (!plants.containsKey(position)) {
                        Plant newPlant = new Plant(position); // Zakładamy, że Plant ma konstruktor z pozycją
                        placePlant(newPlant);
                    }
                }
            }
        }
    }
}

