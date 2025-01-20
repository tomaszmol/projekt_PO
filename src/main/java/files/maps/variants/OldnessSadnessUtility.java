package files.maps.variants;

import files.map_elements.Animal;
import files.maps.WorldMap;

public class OldnessSadnessUtility{
    private static final int INCREASE_MOVE_SKIP_CHANCE_EVERY_X_DAYS = 10;
    private static final int INCREASE_VALUE = 5;
    private static final int INITIAL_PERCENTAGE_CHANCE = 0;
    private static final int MAX_PERCENTAGE_CHANCE = 80;


    public OldnessSadnessUtility() {
    }

    public boolean animalSkippedMove(Animal animal) {
        int chance = Math.min( INITIAL_PERCENTAGE_CHANCE + (animal.getSurvivedDays()% INCREASE_MOVE_SKIP_CHANCE_EVERY_X_DAYS)* INCREASE_VALUE, MAX_PERCENTAGE_CHANCE);
        if (Math.random()*100 <= chance){
            animal.getGenetics().getNextMoveInSequence();
            return true;
        }
        return false;


    }
}
