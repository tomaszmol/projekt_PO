package files.maps;

import files.map_elements.Animal;
import files.map_elements.WorldElement;
import files.util.*;

import java.util.List;
import java.util.UUID;

/**
 * The interface responsible for interacting with the map of the world.
 * Assumes that Vector2d and MoveDirection classes are defined.
 *
 * @author apohllo, idzik
 */
public interface WorldMap extends MoveValidator {

    /**
     * Place a animal on the map.
     *
     * @param animal The animal to place on the map.
     */
    void place(Animal animal) throws Exception;

    /**
     * Moves an animal (if it is present on the map) according to specified direction.
     * If the move is not possible, this method has no effect.
     */
    void move(Animal animal);

    /**
     * Return true if given position on the map is occupied. Should not be
     * confused with canMove since there might be empty positions where the animal
     * cannot move.
     *
     * @param position Position to check.
     * @return True if the position is occupied.
     */
    boolean isOccupied(Vector2d position);

    /**
     * Return an animal at a given position.
     *
     * @param position The position of the animal.
     * @return animal or null if the position is not occupied.
     */
    WorldElement objectAt(Vector2d position);

    /**
     * Get all elements currently on the map.
     *
     * @return List of all elements.
     */
    List<WorldElement> getElements();

    /**
     * Return the current boundaries of the map.
     *
     * @return A Boundary object representing the map's boundaries.
     */
    Boundary getCurrentBounds();

    UUID getId();

    void addObserver(MapChangeListener observer);

    void removeObserver(MapChangeListener observer);

    void notifyObservers(String message);
}
