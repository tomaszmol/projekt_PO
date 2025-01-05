package files.model;

import files.util.Vector2d;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Vector2dTest {

    @Test
    void equals() {
        //given
        Vector2d u = new Vector2d(10,1);
        Vector2d v = new Vector2d(1,10);
        Vector2d w = new Vector2d(1,10);

        //when + then
        Assertions.assertNotEquals(u,v);
        Assertions.assertEquals(v,w);
        Assertions.assertEquals(u,u);
    }

    @Test
    void toStringTest() {
        Vector2d v = new Vector2d(5,7);
        Assertions.assertEquals(v.toString(),"(5, 7)");
    }

    @Test
    void precedes() {
        Vector2d u = new Vector2d(0,5);
        Vector2d v = new Vector2d(10,5);
        Vector2d w = new Vector2d(10,10);

        Assertions.assertTrue(u.precedes(u));

        Assertions.assertTrue(u.precedes(v));
        Assertions.assertFalse(v.precedes(u));

        Assertions.assertTrue(u.precedes(w));

    }

    @Test
    void follows() {
        Vector2d u = new Vector2d(0,5);
        Vector2d v = new Vector2d(10,5);
        Vector2d w = new Vector2d(10,10);

        Assertions.assertTrue(u.follows(u));

        Assertions.assertFalse(u.follows(v));
        Assertions.assertTrue(v.follows(u));

        Assertions.assertFalse(u.follows(w));
    }

    @Test
    void upperRight() {
        Vector2d u = new Vector2d(0,-5);
        Vector2d v = new Vector2d(-150,25);

        Assertions.assertEquals(u.upperRight(v), new Vector2d(0,25));
    }

    @Test
    void lowerRight() {
        Vector2d u = new Vector2d(0,-5);
        Vector2d v = new Vector2d(-150,25);

        Assertions.assertEquals(u.lowerLeft(v), new Vector2d(-150,-5));
    }

    @Test
    void add() {
        Vector2d u = new Vector2d(3,77);
        Vector2d v = new Vector2d(7,33);
        Assertions.assertEquals(u.add(v),new Vector2d(10,110));
    }

    @Test
    void substract() {
        Vector2d u = new Vector2d(3,77);
        Vector2d v = new Vector2d(7,33);
        Assertions.assertEquals(u.subtract(v),new Vector2d(-4,44));
    }

    @Test
    void opposite() {
        Vector2d u = new Vector2d(3,77);
        Assertions.assertEquals(u.opposite(),new Vector2d(-3,-77));
    }

}
