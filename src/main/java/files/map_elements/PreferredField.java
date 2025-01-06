package files.map_elements;

import files.util.Vector2d;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class PreferredField implements WorldElement{

    private final Vector2d position;
    Image preferredFieldImg = null;

    public PreferredField(Vector2d position) {
        this.position = position;

        var res = getClass().getResource("/preferredField.png");
        if (res != null) this.preferredFieldImg = new Image(res.toExternalForm());
    }

    public Vector2d getPosition() {
        return position;
    }

    public String toString() {
        return "&";
    }

    @Override
    public Image getImage() {
        return preferredFieldImg;
    }

    @Override
    public Color getElementColour() {
        return Color.LIGHTGREEN;
    }

    @Override
    public double getElementSizeMultiplier() {
        return 0.9;
    }

}
