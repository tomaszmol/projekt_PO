package files.map_elements;

import files.util.Vector2d;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public interface WorldElement {

    Vector2d getPosition();
    String toString();
    Image getImage();
    Color getElementColour();
    double getElementSizeMultiplier();
    boolean hasImage();
}
