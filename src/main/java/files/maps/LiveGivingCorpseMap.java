package files.maps;

import files.map_elements.PreferredField;
import files.simulation.SimulationParams;
import files.util.Boundary;
import files.util.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LiveGivingCorpseMap extends AnimalManager {

    public LiveGivingCorpseMap(SimulationParams params) {
        super(params);
        generatePreferredFields();
        super.growPlantsOnWholeMap(params.initialPlantNumber());
    }

    private void generatePreferredFields() {
        Boundary bounds = getCurrentBounds();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();

        int mapWidth = upperRight.getX() - lowerLeft.getX() + 1;
        int mapHeight = upperRight.getY() - lowerLeft.getY() + 1;
        int fieldsNumber = mapWidth * mapHeight;

        int preferredFieldsCount = (int) (fieldsNumber * PREFERRED_ZONE_RATIO);


        // Dodajemy preferowane pola w pełnych parach rzędów
        List<Vector2d> availableFields = new ArrayList<>();
        for (int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
            for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
                availableFields.add(new Vector2d(x, y));
            }
        }
        Random random = new Random();
        Collections.shuffle(availableFields, random);  // Losowo mieszamy dostępne pola

        for (int i = 0; i < preferredFieldsCount; i++) {
            Vector2d position = availableFields.get(i);  // Wybieramy pole z mieszanej listy
            preferredFields.put(position, new PreferredField(position));
        }


    }

}
