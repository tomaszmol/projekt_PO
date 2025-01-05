package files.maps;

import files.map_elements.Animal;
import files.map_elements.WorldElement;
import files.util.Boundary;
import files.util.MoveDirection;
import files.util.Vector2d;

import java.util.List;

public class RectEquatorMap extends EquatorMap {

    private final int mapWidth;
    private final int mapHeight;

    public RectEquatorMap(int width, int height, int equatorHeight) {
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
    public void place(Animal animal) throws Exception {
        super.place(animal);
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        // Pobierz aktualne granice mapy
        Boundary bounds = getCurrentBounds();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();

        // Sprawdź, czy pozycja znajduje się w granicach
        return position.follows(lowerLeft)
                && position.precedes(upperRight)
                && !isOccupied(position);
    }

}
