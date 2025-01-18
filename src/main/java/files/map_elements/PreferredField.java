package files.map_elements;

import files.util.Vector2d;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class PreferredField implements WorldElement{

    private final Vector2d position;
    Image preferredFieldImg = null;
    private boolean isPlantGrown = false;

    public PreferredField(Vector2d position) {
        this.position = position;

        var res = getClass().getResource("/preferredFiel.png");
        if (res != null) this.preferredFieldImg = new Image(res.toExternalForm());
    }

    public Vector2d getPosition() {
        return position;
    }

    public String toString() {
        return "&";
    }

    public Image getImage() {
        return preferredFieldImg;
    }
    public Color getElementColour() {
        return Color.LIGHTGREEN;
    }
    public double getElementSizeMultiplier() {
        return 0.98;
    }
    public boolean hasImage() { return preferredFieldImg != null; }

    public boolean isPlantGrown() {
        return isPlantGrown;
    }

    public void setPlantGrown(boolean plantGrown) {
        isPlantGrown = plantGrown;
    }


}
