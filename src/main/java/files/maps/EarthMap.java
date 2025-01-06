package files.maps;

import files.map_elements.Animal;
import files.map_elements.Plant;
import files.map_elements.WorldElement;
import files.util.*;

import java.util.*;

public abstract class EarthMap implements WorldMap {

    protected HashMap<Vector2d, List<Animal>> animals; //przechowywanie zwierzakow jako listy zwierzakow na danej pozycji, poniewaz moze byc kilka zwierzakow na jedynm polu
    protected HashMap<Vector2d, Plant> plants; // roslina jest tylko jedna na danej pozycji
    private final List<MapChangeListener> observers = new ArrayList<>();
    private final UUID id;

    public EarthMap() {
        this.animals = new HashMap<>();
        this.plants = new HashMap<>();
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
        Vector2d oldPos = animal.getPosition();
        MapDirection oldOrientation = animal.getOrientation();

        if (animal.move(this)) {
            animals.remove(oldPos);
            animals.put(animal.getPosition(), animal);
            notifyObservers("Animal moved from " + oldPos + " to " + animal.getPosition());
        }
        else {
            MapDirection newOrientation = animal.getOrientation();
            if (oldOrientation != newOrientation) {
                notifyObservers("Animal changed orientation from " + oldOrientation + " to " + newOrientation);
            }
            else {
                notifyObservers("Animal did not move because new position will be beyond borders.");
            }

        }
    }

    public boolean isOccupied(Vector2d position) {
        return animals.get(position) != null;
    }

    public WorldElement objectAt(Vector2d position) {
        return animals.get(position);
    }

    public List<WorldElement> getElements() {
        return new ArrayList<>(animals.values());
    }

    protected boolean isPositionValid(Vector2d position) {
        return true; // Default behavior
    }

    public PositionAndOrientation positionAndOrientationAfterMovement(Vector2d origin, MapDirection currentOrientation, MoveDirection direction){
        Vector2d shift = currentOrientation.toUnitVector();
        if (direction == MoveDirection.BACKWARD) shift = shift.multiplyByScalar(-1);
        return new PositionAndOrientation(origin.add(shift),currentOrientation);
    }

    @Override
    public void place(Animal animal) throws Exception {
        if (!isPositionValid(animal.getPosition())) {
            throw new IncorrectPositionException(animal.getPosition());
        }

        if (isOccupied(animal.getPosition())) {
            throw new IncorrectPositionException(animal.getPosition());
        }

        animals.put(animal.getPosition(), animal);
        notifyObservers("Animal placed at " + animal.getPosition());
    }

    public String toString() {
        Boundary bounds = getCurrentBounds();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();
        return new MapVisualizer(this).draw(lowerLeft, upperRight);
    }

    @Override
    public UUID getId() {
        return this.id;
    }




}
