package files.map_elements;

import files.util.Vector2d;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public interface WorldElement {

    Vector2d getPosition();
    String toString();

    static boolean isWithinMapBoundaries(Vector2d position, Vector2d[] boundary) {
        return boundary[0].precedes(position) && boundary[1].follows(position);
    }
    Image getImage();
    Color getElementColour();
    double getElementSizeMultiplier();
}
