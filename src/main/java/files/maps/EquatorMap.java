package files.maps;

import files.map_elements.PreferredField;
import files.simulation.SimulationParams;
import files.util.Boundary;
import files.util.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EquatorMap extends AnimalManager {

    public EquatorMap(SimulationParams params) {
        super(params);
        generatePreferredFields();
        super.growPlantsOnWholeMap();
    }

    private void generatePreferredFields() {
        // Określamy rozmiar mapy
        Boundary bounds = getCurrentBounds();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();

        int mapWidth = upperRight.getX() - lowerLeft.getX() + 1;
        int mapHeight = upperRight.getY() - lowerLeft.getY() + 1;
        int fieldsNumber = mapWidth * mapHeight;

        int expectedNumberOfPreferredFields = (int) Math.ceil(fieldsNumber * PREFERRED_ZONE_RATIO);

        // Obliczanie preferowanych rzędów
        if (mapHeight % 2 == 0) {
            // MapHeight parzysta
            int gorny = (mapHeight / 2);
            int dolny = (mapHeight / 2) - 1;
            generateInRows(lowerLeft, upperRight, mapWidth, mapHeight, gorny, dolny, expectedNumberOfPreferredFields);

        } else {
            // MapHeight nieparzysta---
            int rownik = mapHeight / 2; // Centralny rząd---------------------------------
            int gorny = rownik + 1;
            int dolny = rownik - 1;
            int fieldsLeft = expectedNumberOfPreferredFields;

            if (fieldsLeft >= mapWidth) {
                for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
                    preferredFields.put(new Vector2d(x, rownik), new PreferredField(new Vector2d(x, rownik)));
                    fieldsLeft--;
                }
            }
            else {
                List<Vector2d> remainingPositions = new ArrayList<>();
                for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
                    remainingPositions.add(new Vector2d(x, rownik));
                }
                Collections.shuffle(remainingPositions);
                for (int i = 0; fieldsLeft > 0 & i<mapWidth; i++) {
                    Vector2d position = remainingPositions.get(i);
                    preferredFields.put(position, new PreferredField(position));
                    fieldsLeft--;
                }
            }

            generateInRows(lowerLeft, upperRight, mapWidth, mapHeight, gorny, dolny, fieldsLeft);

        }
    }

    private void generateInRows(Vector2d lowerLeft, Vector2d upperRight, int mapWidth, int mapHeight, int gorny, int dolny, int fieldsLeft) {
        while (gorny < mapHeight & dolny >= 0 & fieldsLeft >= 2*mapWidth) {
            for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
                preferredFields.put(new Vector2d(x, gorny), new PreferredField(new Vector2d(x, gorny)));
                fieldsLeft--;
            }
            for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
                preferredFields.put(new Vector2d(x, dolny), new PreferredField(new Vector2d(x, dolny)));
                fieldsLeft--;
            }
            gorny++;
            dolny--;
        }

        Random random = new Random();
        List<Vector2d> remainingPositions1 = new ArrayList<>();
        List<Vector2d> remainingPositions2 = new ArrayList<>();
        for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
            if (random.nextInt(2) == 0){
                remainingPositions1.add(new Vector2d(x, gorny));
                remainingPositions2.add(new Vector2d(x, dolny));
            }
            else {
                remainingPositions1.add(new Vector2d(x, dolny));
                remainingPositions2.add(new Vector2d(x, gorny));
            }
        }
        Collections.shuffle(remainingPositions1);
        for (int i = 0; fieldsLeft>0 & i<mapWidth; i++) {
            Vector2d position = remainingPositions1.get(i);
            preferredFields.put(position, new PreferredField(position));
            fieldsLeft--;

        }
        Collections.shuffle(remainingPositions2);
        for (int i = 0; fieldsLeft > 0 & i<mapWidth; i++) {
            Vector2d position = remainingPositions2.get(i);
            preferredFields.put(position, new PreferredField(position));
            fieldsLeft--;
        }
    }

}


