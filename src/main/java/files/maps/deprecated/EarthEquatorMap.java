package files.maps.deprecated;

import files.map_elements.Animal;
import files.map_elements.WorldElement;
import files.util.*;

import java.util.List;

public class EarthEquatorMap extends EquatorMap {

    private final int mapWidth;
    private final int mapHeight;

    public EarthEquatorMap(int width, int height, int equatorHeight) {
        super(width, height, equatorHeight);
        this.mapWidth = width;
        this.mapHeight = height;
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        WorldElement animal = super.objectAt(position);
        if (animal != null)
            return animal;

        return super.getPlants().get(position);
    }

    @Override
    public List<WorldElement> getElements() {
        List<WorldElement> output = super.getElements();
        output.addAll(super.getPlants().values());
        return output;
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0,0),new Vector2d(mapWidth,mapHeight));
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return !isOccupied(position);
    }
    @Override
    public PositionAndOrientation positionAndOrientationAfterMovement(Vector2d origin, MapDirection currentOrientation, MoveDirection direction){
        PositionAndOrientation afterMovement = super.positionAndOrientationAfterMovement(origin, currentOrientation, direction);
        Vector2d afterPos = afterMovement.pos();

        // overflow to other side of map
        int x = afterPos.getX();
        if (x<0) afterPos.setX(mapWidth+x);
        if (x>mapWidth) afterPos.setX(x-mapWidth-1);

        // reorient on poles
        int y = afterPos.getY();
        if (y<=0 || y>=mapHeight) return new PositionAndOrientation(afterPos,currentOrientation.reverse());
        else return new PositionAndOrientation(afterPos,currentOrientation);
    }

    @Override
    public void place(Animal animal) throws Exception {
        super.place(animal);
    }

}
