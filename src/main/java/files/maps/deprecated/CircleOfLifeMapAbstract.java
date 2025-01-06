package files.maps.deprecated;

import files.map_elements.Plant;
import files.maps.AbstractEarthMap;
import files.util.Vector2d;

import java.util.HashMap;
import java.util.Map;

public abstract class CircleOfLifeMapAbstract extends AbstractEarthMap {

    double steppeInitPlantChange = 0.2;

    private final Map<Vector2d, Plant> plants;

    public CircleOfLifeMapAbstract(int width, int height) {
        super();

        plants = new HashMap<>();

//        // jungle plants
//        for (int i = 0; i <= jungleWidth; i++) {
//            for (int j = 0; j <= jungleHeight; j++) {
//                if (Math.random() < jungleInitPlantChance) {
//                    Vector2d pos = new Vector2d(width / 2 - jungleWidth / 2 + i, height / 2 - jungleHeight / 2 + j);
//                    plants.put(pos, new Plant(pos));
//                }
//            }
//        }

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
