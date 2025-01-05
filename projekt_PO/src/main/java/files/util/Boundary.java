package files.util;

public record Boundary(Vector2d lowerLeft, Vector2d upperRight) {

    public int getLowerX() { return lowerLeft.getX(); }
    public int getLowerY() { return lowerLeft.getY(); }
    public int getUpperX() { return upperRight.getX(); }
    public int getUpperY() { return upperRight.getY(); }
}
