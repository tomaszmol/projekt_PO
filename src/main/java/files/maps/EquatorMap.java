package files.maps;

import files.map_elements.Plant;
import files.util.Boundary;
import files.util.Vector2d;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class EquatorMap extends AbstractEarthMap {

    private static final double PREFERRED_ZONE_RATIO = 0.2; // 20% mapy to preferowany obszar
    private static final double PREFERRED_ZONE_PROBABILITY = 0.8; // 80% szansy na preferowane pole
    private static final double NON_PREFERRED_ZONE_PROBABILITY = 0.2; // 20% szansy na niepreferowane pole

    public EquatorMap(int mapWidth, int mapHeight) {
        super(mapWidth, mapHeight);
        placePlants();
    }

    public void placePlants() {
        Random random = new Random();

        // Określamy rozmiar mapy (zakładając, że mamy metodę, która zwraca rozmiar mapy)
        Boundary bounds = getCurrentBounds();
        Vector2d lowerLeft = bounds.lowerLeft();
        Vector2d upperRight = bounds.upperRight();

        int mapWidth = upperRight.getX() - lowerLeft.getX() + 1;
        int mapHeight = upperRight.getY() - lowerLeft.getY() + 1;

        // Określenie zakresu preferowanego pasa (równika)
        int equatorHeight = mapHeight / 2;
        int equatorRange = (int) (mapHeight * PREFERRED_ZONE_RATIO); // 20% wysokości mapy

        // Wybieramy, czy roślina ma wyrosnąć na preferowanym czy mniej preferowanym polu
        boolean isPreferred = random.nextDouble() < PREFERRED_ZONE_PROBABILITY;

        // Losowanie pozycji
        Vector2d plantPosition;
        if (isPreferred) {
            // Jeśli preferowane pole, losujemy w okolicach równika
            int y = random.nextInt(equatorRange) + (equatorHeight - equatorRange / 2);
            int x = random.nextInt(mapWidth);
            plantPosition = new Vector2d(x, y);
        } else {
            // Jeśli mniej preferowane pole, losujemy w pozostałej części mapy
            int y = random.nextInt(mapHeight - equatorRange);
            int x = random.nextInt(mapWidth);
            plantPosition = new Vector2d(x, y);
        }

        // Dodajemy roślinę na wybranej pozycji
        if (!isPositionValid(plantPosition)) {
            throw new IllegalArgumentException("Niepoprawna pozycja rośliny: " + plantPosition);
        }

        plants.put(plantPosition, plant);
        notifyObservers("Plant placed at " + plantPosition);
    }

}
