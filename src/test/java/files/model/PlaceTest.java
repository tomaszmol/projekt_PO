package files.model;

import files.map_elements.Animal;
import files.util.IncorrectPositionException;
import files.maps.RectCircleOfLifeMap;
import files.util.Vector2d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaceTest {

    @Test
    void testPlaceValidAnimal() throws Exception {
        RectCircleOfLifeMap map = new RectCircleOfLifeMap(5, 5);
        Animal animal = new Animal(new Vector2d(2, 2));

        assertDoesNotThrow(() -> map.place(animal));
        assertTrue(map.isOccupied(new Vector2d(2, 2)));
        assertEquals(animal, map.objectAt(new Vector2d(2, 2)));
    }

    @Test
    void testPlaceAnimalOutsideBoundaries() {
        RectCircleOfLifeMap map = new RectCircleOfLifeMap(5, 5);
        Animal animal = new Animal(new Vector2d(6, 6)); // Outside map boundaries

        Exception exception = assertThrows(IncorrectPositionException.class, () -> map.place(animal));
        assertEquals("Position (6, 6) is not correct.", exception.getMessage());
    }

    @Test
    void testPlaceAnimalOnOccupiedPosition() throws Exception {
        RectCircleOfLifeMap map = new RectCircleOfLifeMap(5, 5);
        Animal animal1 = new Animal(new Vector2d(2, 2));
        Animal animal2 = new Animal(new Vector2d(2, 2)); // Same position as animal1

        map.place(animal1);
        Exception exception = assertThrows(IncorrectPositionException.class, () -> map.place(animal2));
        assertEquals("Position (2, 2) is not correct.", exception.getMessage());
    }
}
