package files.map_elements;

import files.util.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Animal implements WorldElement {

    private Vector2d position;
    private MapDirection orientation = MapDirection.NORTH;
    private int age;
    private int numberOfChildren;
    private int numberOfDescendants;
    private int energy;
    Genetics genes;
    StatisticsTracker stats;



    Image animalImg = null;

    public Animal(Vector2d position) {
        this.position = position;
        age = 0;

        genes = new Genetics();
        stats = new StatisticsTracker();

        var res = getClass().getResource("/animal.png");
        if (res != null) this.animalImg = new Image(res.toExternalForm());
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public Vector2d getPosition() {
        return position;
    }

    public String toString() {
        return switch (orientation) {
            case EAST -> ">";
            case NORTH -> "^";
            case SOUTH -> "v";
            case WEST -> "<";
        };
    }

    @Override
    public Image getImage() {
        return animalImg;
    }

    @Override
    public Color getElementColour() {
        return Color.BROWN;
    }

    @Override
    public double getElementSizeMultiplier() {
        return 0.5;
    }

    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }

    // returns true when position changed (rotation change still returns false)
    public boolean move(MoveValidator moveValidator) {
        var movement = genes.getNextMoveInSequence();

        // rotation
        if (movement == MoveDirection.RIGHT) {
            orientation = orientation.next();
            return false;
        }
        if (movement == MoveDirection.LEFT) {
            orientation = orientation.previous();
            return false;
        }

        // movement
        PositionAndOrientation pno = moveValidator.positionAndOrientationAfterMovement(position,orientation,movement);
        orientation = pno.orientation();

        if (moveValidator.canMoveTo(pno.pos())) {
            position = pno.pos();
            return true;
        }
        else return false;
    }

    public String getAnimalInfo() {
        return String.format(
                "Energy: %d\nAge: %d\nPosition: %s\nGenes: %s",
                energy, age, position, genes.toString()
        );
    }
}