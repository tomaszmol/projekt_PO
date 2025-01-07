package files.maps;

import files.map_elements.PreferredField;
import files.simulation.SimulationParams;
import files.util.Boundary;
import files.util.Vector2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class EquatorMap extends AnimalManager {

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

        int expectedNumberOfPreferredFields = (int) (fieldsNumber * PREFERRED_ZONE_RATIO);

        // Obliczanie preferowanych rzędów
        if (mapHeight % 2 == 0) {
            // MapHeight parzysta
            int fullPairOfRows = expectedNumberOfPreferredFields / (mapWidth * 2);
            int fieldsLeft = expectedNumberOfPreferredFields - (fullPairOfRows * mapWidth * 2);

            int lowerEquator = (mapHeight / 2) - fullPairOfRows;
            int upperEquator = (mapHeight / 2) + fullPairOfRows - 1;

            // Dodajemy preferowane pola w pełnych parach rzędów
            for (int y = lowerEquator; y <= upperEquator; y++) {
                for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
                    preferredFields.put(new Vector2d(x, y), new PreferredField(new Vector2d(x, y)));
                }
            }

            // Rozdzielanie pozostałych pól losowo na sąsiednich rzędach
            Random random = new Random();
            List<Vector2d> remainingPositions = new ArrayList<>();
            for (int y = lowerEquator - 1; y >= lowerLeft.getY(); y--) {
                for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
                    remainingPositions.add(new Vector2d(x, y));
                }
            }
            for (int y = upperEquator + 1; y <= upperRight.getY(); y++) {
                for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
                    remainingPositions.add(new Vector2d(x, y));
                }
            }
            Collections.shuffle(remainingPositions);
            for (int i = 0; i < fieldsLeft; i++) {
                Vector2d position = remainingPositions.get(i);
                preferredFields.put(position, new PreferredField(position));
            }

        } else {
            // MapHeight nieparzysta
            int equatorRow = mapHeight / 2; // Centralny rząd
            int fullPairOfRows = (expectedNumberOfPreferredFields - mapWidth) / (mapWidth * 2);
            int fieldsLeft = expectedNumberOfPreferredFields - (mapWidth + fullPairOfRows * mapWidth * 2);

            int lowerEquator = equatorRow - fullPairOfRows;
            int upperEquator = equatorRow + fullPairOfRows;

            // Dodajemy preferowane pola w centralnym rzędzie
            for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
                preferredFields.put(new Vector2d(x, equatorRow), new PreferredField(new Vector2d(x, equatorRow)));
            }

            // Dodajemy preferowane pola w pozostałych pełnych parach rzędów
            for (int y = lowerEquator; y <= upperEquator; y++) {
                if (y == equatorRow) continue; // Pomijamy już dodany centralny rząd
                for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
                    preferredFields.put(new Vector2d(x, y), new PreferredField(new Vector2d(x, y)));
                }
            }

            // Rozdzielanie pozostałych pól losowo na sąsiednich rzędach
            Random random = new Random();
            List<Vector2d> remainingPositions = new ArrayList<>();
            for (int y = lowerEquator - 1; y >= lowerLeft.getY(); y--) {
                for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
                    remainingPositions.add(new Vector2d(x, y));
                }
            }
            for (int y = upperEquator + 1; y <= upperRight.getY(); y++) {
                for (int x = lowerLeft.getX(); x <= upperRight.getX(); x++) {
                    remainingPositions.add(new Vector2d(x, y));
                }
            }
            Collections.shuffle(remainingPositions);
            for (int i = 0; i < fieldsLeft; i++) {
                Vector2d position = remainingPositions.get(i);
                preferredFields.put(position, new PreferredField(position));
            }
        }
    }

}


