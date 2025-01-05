package files.model;


import files.util.MapDirection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapDirectionTest {

    @Test
    public void next() {
        MapDirection dir = MapDirection.EAST;

        dir = dir.next();
        Assertions.assertEquals(dir, MapDirection.SOUTH);

        dir = dir.next();
        Assertions.assertEquals(dir, MapDirection.WEST);

        dir = dir.next();
        Assertions.assertEquals(dir, MapDirection.NORTH);

        dir = dir.next();
        Assertions.assertEquals(dir, MapDirection.EAST);
    }

    @Test
    void previous() {
        MapDirection dir = MapDirection.EAST;

        dir = dir.previous();
        Assertions.assertEquals(dir, MapDirection.NORTH);

        dir = dir.previous();
        Assertions.assertEquals(dir, MapDirection.WEST);

        dir = dir.previous();
        Assertions.assertEquals(dir, MapDirection.SOUTH);

        dir = dir.previous();
        Assertions.assertEquals(dir, MapDirection.EAST);
    }

}