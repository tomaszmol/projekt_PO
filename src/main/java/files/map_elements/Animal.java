package files.map_elements;

import files.util.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Animal implements WorldElement {

    private Vector2d position;
    private MapDirection orientation;
    private int survivedDays;
    private int numberOfChildren;
    private int numberOfDescendants;
    private int energy;

    Genetics genes;

    Animal mother = null;
    Animal father = null;
    Image animalImg = null;
    public Animal(Vector2d position, int geneNumber) {
        this.position = position;
        survivedDays = 0;
        numberOfChildren = 0;
        numberOfDescendants = 0;
        energy = 12;
        this.orientation = MapDirection.getMapDirection((int) (Math.random()*8));

        genes = new Genetics(geneNumber);

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
            case NORTH_EAST -> "NE";
            case EAST -> ">";
            case NORTH -> "^";
            case SOUTH_EAST -> "SE";
            case SOUTH -> "v";
            case SOUTH_WEST -> "SW";
            case WEST -> "<";
            case NORTH_WEST -> "NW";
        };
    }

    @Override
    public Image getImage() {
        return animalImg;
    }

    public Genetics getGenetics() {
        return genes;
    }

    public int getEnergy() {
        return energy;
    }

    public void useEnergy(int amount) {
        energy = Math.max(0,energy-amount);
    }
    @Override
    public Color getElementColour() {
        return Color.BROWN;
    }
    @Override
    public double getElementSizeMultiplier() {
        return 0.7;
    }

    @Override
    public boolean hasImage() {
        return animalImg != null;
    }

    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }


    // returns true when position changed (rotation change still returns false)
    public boolean move(MoveValidator moveValidator, int energyCostPerMove) {
        var movement = genes.getNextMoveInSequence();
        useEnergy(energyCostPerMove);
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

    public void addDescendant () { // bedzie kaskadowac az do samej gory
        numberOfDescendants++;
        if (mother != null) mother.addDescendant();
        if (father != null) father.addDescendant();
    }
    public void addChild () {
        numberOfChildren++; numberOfDescendants++; // czy dziecko to od razu potomek ?? imo tak
        if (mother != null) mother.addDescendant();
        if (father != null) father.addDescendant();
    }

    public void setParents(Animal mother, Animal father) {
        this.mother = mother;
        this.father = father;
    }
    public String getAnimalInfo() {
        return String.format(
                "Energy: %d\nAge: %d\nPosition: %s\nGenetic code: %s\nNumber of Children: %d\nNumber of Descendands: %d",
                energy, survivedDays, position, genes.toString(), numberOfChildren, numberOfDescendants
        );
    }

    public int getSurvivedDays() {
        return survivedDays;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public void eatFood(int energyProfit) {
        energy += energyProfit;
    }
}