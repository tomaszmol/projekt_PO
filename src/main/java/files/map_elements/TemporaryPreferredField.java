package files.map_elements;

import files.util.Vector2d;
import javafx.scene.paint.Color;

public class TemporaryPreferredField extends PreferredField {
    private int daysToLive;
    int daysCounter = 0;
    public TemporaryPreferredField(Vector2d position, int daysToLive) {
        super(position);
        this.daysToLive = daysToLive;
    }

    public void incrementDaysCounter() {
        daysCounter++;
    }

    @Override
    public Color getElementColour() {
        return Color.LAWNGREEN;
    }

    public int getDaysToLive() {
        return daysToLive;
    }

    public boolean isTooOld() {
        if (daysCounter >= daysToLive) {
            return true;
        }
        return false;
    }
}
