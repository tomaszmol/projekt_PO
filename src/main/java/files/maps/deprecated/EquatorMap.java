package files.maps.deprecated;

import files.map_elements.Plant;
import files.maps.AbstractWorldMap;
import files.util.Vector2d;

import java.util.HashMap;
import java.util.Map;

public abstract class EquatorMap extends AbstractWorldMap {

    double equatorInitPlantChance = 0.8;
    double steppeInitPlantChange = 0.2;

    private final Map<Vector2d, Plant> plants;

    public EquatorMap(int width, int height, int equatorHeight) {
        super();

        plants = new HashMap<>();

        // jungle plants
        for (int i = 0; i <= width; i++) {
            for (int j = 0; j <= equatorHeight; j++) {
                if (Math.random() < equatorInitPlantChance) {
                    Vector2d pos = new Vector2d(i, height / 2 - equatorHeight / 2 + j);
                    plants.put(pos, new Plant(pos));
                }
            }
        }

        // steppe plants
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Vector2d pos =new Vector2d(i, j);
                if (!plants.containsKey(pos) && Math.random() < steppeInitPlantChange) {
                    plants.put(pos, new Plant(pos));
                }
            }
        }
    }

    public Map<Vector2d, Plant> getPlants() {
        return plants;
    }
}
