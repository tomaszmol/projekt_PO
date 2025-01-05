package files.util;

import java.util.Objects;

public class Vector2d {

    private int x;
    private int y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2d random(int firstInRange, int lastInRange) {
        return new Vector2d(
                (int)Math.round(firstInRange + Math.random() * Math.sqrt((lastInRange - firstInRange) * 10)),
                (int)Math.round(firstInRange + Math.random() * Math.sqrt((lastInRange - firstInRange) * 10))
        );
    }
    public static Vector2d randomInBounds(Boundary b) {
        return new Vector2d(
                (int)Math.round(b.getLowerX() + Math.random() * (b.getUpperX() - b.getLowerY()) ),
                (int)Math.round(b.getLowerY() + Math.random() * (b.getUpperY() - b.getLowerY()) )
        );
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public boolean precedes(Vector2d other) {
        return x <= other.x && y <= other.y;
    }

    public boolean follows(Vector2d other) {
        return x >= other.x && y >= other.y;
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(x + other.x, y + other.y);
    }

    public Vector2d subtract(Vector2d other) {
        return new Vector2d(x - other.x, y - other.y);
    }

    public Vector2d multiplyByScalar(int scalar) {
        return new Vector2d(x * scalar, y * scalar);
    }

    public Vector2d upperRight(Vector2d other) {
        return new Vector2d(Math.max(x,other.x),Math.max(y,other.y));
    }

    public Vector2d lowerLeft(Vector2d other) {
        return new Vector2d(Math.min(x,other.x),Math.min(y,other.y));
    }

    public Vector2d opposite() {
        return new Vector2d(-x, -y);
    }

    public boolean equals(Object other) {
        return other instanceof Vector2d && (((Vector2d) other).x == x && ((Vector2d) other).y == y);
    }

    public int hashCode() {
        return Objects.hash(x,y);
    }

}
