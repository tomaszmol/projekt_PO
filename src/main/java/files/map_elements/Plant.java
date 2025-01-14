package files.map_elements;

import files.util.Vector2d;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Plant implements WorldElement {

    private final Vector2d position;
    Image plantImg = null;

    public Plant(Vector2d position) {
        this.position = position;

        var res = getClass().getResource("/plant.png");
        if (res != null) this.plantImg = new Image(res.toExternalForm());
    }

    public Vector2d getPosition() {
        return position;
    }

    public String toString() {
        return "*";
    }

    @Override
    public Image getImage() {
        return plantImg;
    }

    @Override
    public Color getElementColour() {
        return Color.DARKGREEN;
    }

    @Override
    public double getElementSizeMultiplier() {
        return 0.8;
    }

    @Override
    public boolean hasImage() {
        return plantImg != null;
    }

}
