package files.map_elements;

import files.util.Vector2d;
import javafx.scene.paint.Color;

public class TemporaryPreferredField extends PreferredField {
    public TemporaryPreferredField(Vector2d position) {
        super(position);
    }

    @Override
    public Color getElementColour() {
        return Color.DARKGREEN;
    }

}
