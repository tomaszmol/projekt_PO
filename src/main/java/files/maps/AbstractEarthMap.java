package files.maps;

import files.map_elements.*;
import files.simulation.SimulationParams;
import files.util.*;

import java.util.*;

public abstract class AbstractEarthMap implements WorldMap {
    protected static final double PREFERRED_ZONE_RATIO = 0.2;
    protected HashMap<Vector2d, List<Animal>> animals; //przechowywanie zwierzakow jako listy zwierzakow na danej pozycji, poniewaz moze byc kilka zwierzakow na jedynm polu
    protected HashMap<Vector2d, Plant> plants;
    protected HashMap<Vector2d, PreferredField> preferredFields;// roslina jest tylko jedna na danej pozycji
    private final List<MapChangeListener> observers = new ArrayList<>();
    private final UUID id;
    private final Vector2d mapDimensions;
    SimulationParams params;

    public AbstractEarthMap(SimulationParams params) {
        this.mapDimensions = new Vector2d(params.mapWidth() - 1, params.mapHeight() - 1);
        this.animals = new HashMap<>();
        this.plants = new HashMap<>();
        this.preferredFields = new HashMap<>();
        this.id = UUID.randomUUID();
        this.params = params;
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
                        Plant newPlant = new Plant(position);
                        plants.put(position, newPlant);
                    }
                }
            }
        }
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
    public HashMap<Vector2d, Plant> getPlants() {
        return plants;
    }

    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), mapDimensions);
    }

    public boolean canMoveTo(Vector2d position) {
        return position.follows(new Vector2d(0, 0)) && position.precedes(mapDimensions);
    }


    @Override
    public PositionAndOrientation positionAndOrientationAfterMovement(Vector2d origin, MapDirection currentOrientation, MoveDirection direction){
        Vector2d shift = currentOrientation.toUnitVector();
        if (direction == MoveDirection.BACKWARD) shift = shift.multiplyByScalar(-1);
        PositionAndOrientation afterMovement = new PositionAndOrientation(origin.add(shift),currentOrientation);

        Vector2d afterPos = afterMovement.pos();

        int mapWidth = params.mapWidth();
        int mapHeight = params.mapHeight();

        // overflow to other side of map
        int x = afterPos.getX();
        if (x<0) afterPos.setX(mapWidth+x);
        if (x>mapWidth) afterPos.setX(x-mapWidth-1);

        // reorient on poles
        int y = afterPos.getY();
        if (y<0 || y>mapHeight-2) return new PositionAndOrientation(afterPos,currentOrientation.reverse());
        else return new PositionAndOrientation(afterPos,currentOrientation);
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
}

