package files.maps;

import files.map_elements.PreferredField;
import files.map_elements.TemporaryPreferredField;
import files.simulation.SimulationParams;
import files.util.Boundary;
import files.util.Vector2d;

import java.util.*;

public class LiveGivingCorpseMap extends AnimalManager {

    private static final int DAYS_TO_LIVE = 10; // liczba dni jak długo ma istnieć TemporaryPreferredField

    public LiveGivingCorpseMap(SimulationParams params) {
        super(params);
        generatePreferredFields();
        super.growPlants(params.initialPlantNumber());
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

    @Override
    public List<Vector2d> removeDeadAnimals(int currentDay){
        List<Vector2d> deadPositions = super.removeDeadAnimals(currentDay);
        incrementTemporaryPreferredFields();
        removeTooOldTemporaryPreferredFields();
        generateTemporaryPreferredField(deadPositions);
        return deadPositions;
    }

    private void incrementTemporaryPreferredFields() {
        for (Vector2d position : preferredFields.keySet()) {
            PreferredField field = preferredFields.get(position);
            if (field instanceof TemporaryPreferredField) {
                ((TemporaryPreferredField) field).incrementDaysCounter();
            }
        }
    }

    private void removeTooOldTemporaryPreferredFields() {
        List<Vector2d> positionsToRemove = new ArrayList<>();
        for (Vector2d position : preferredFields.keySet()) {
            PreferredField field = preferredFields.get(position);
            if (field instanceof TemporaryPreferredField && (((TemporaryPreferredField) field).isTooOld())) {
                positionsToRemove.add(position);
            }
        }
        for (Vector2d position : positionsToRemove) {
            preferredFields.remove(position);
        }
    }




    public void generateTemporaryPreferredField(List<Vector2d> deadPositions) {
        Boundary bounds = getCurrentBounds();
        for (Vector2d position : deadPositions) {
            // List to store the positions to check
            List<Vector2d> positionsToCheck = new ArrayList<>();

            // Add the current position (where the animal died)
            positionsToCheck.add(position);

            // Add all neighboring positions
            positionsToCheck.add(new Vector2d(position.getX() + 1, position.getY()));
            positionsToCheck.add(new Vector2d(position.getX() - 1, position.getY()));
            positionsToCheck.add(new Vector2d(position.getX(), position.getY() + 1));
            positionsToCheck.add(new Vector2d(position.getX(), position.getY() - 1));
            positionsToCheck.add(new Vector2d(position.getX() + 1, position.getY() + 1));
            positionsToCheck.add(new Vector2d(position.getX() - 1, position.getY() - 1));
            positionsToCheck.add(new Vector2d(position.getX() + 1, position.getY() - 1));
            positionsToCheck.add(new Vector2d(position.getX() - 1, position.getY() + 1));

            System.out.println("Positions to check: " + positionsToCheck);
            // Iterate through the positions to check for preferredField
            for (Vector2d pos : positionsToCheck) {
                if (pos.getX() < bounds.lowerLeft().getX()){
                    pos = new Vector2d(bounds.upperRight().getX(), pos.getY());
                } else if (pos.getX() > bounds.upperRight().getX()){
                    pos = new Vector2d(bounds.lowerLeft().getX(), pos.getY());
                }
                if (pos.follows(bounds.lowerLeft()) && pos.precedes(bounds.upperRight())) {
                    if (preferredFields.containsKey(pos)){
                        PreferredField field = preferredFields.get(pos);
                        if (field instanceof TemporaryPreferredField) {
                            preferredFields.put(pos, new TemporaryPreferredField(pos, DAYS_TO_LIVE));
                        }
                    }
                    else {
                        preferredFields.put(pos, new TemporaryPreferredField(pos, DAYS_TO_LIVE));
                    }

                    // else do nothing - pole jest preferowane od stworzenia mapy, nie ma potrzeby, aby obecnie było tylko czasowo preferowane

                }
            }
        }

        notifyObservers("TemporaryPreferredField have been created");
    }

}
