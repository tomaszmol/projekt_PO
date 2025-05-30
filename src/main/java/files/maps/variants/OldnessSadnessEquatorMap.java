package files.maps.variants;

import files.map_elements.Animal;
import files.maps.EquatorMap;
import files.simulation.SimulationParams;

public class OldnessSadnessEquatorMap extends EquatorMap {
    public OldnessSadnessEquatorMap(SimulationParams params) {
        super(params);
    }

    @Override
    public void moveAnimal(Animal animal) {
        OldnessSadnessUtility utility = new OldnessSadnessUtility();
        if (utility.animalSkippedMove(animal)) {
            animal.useEnergy(params.energyCostPerMove());
            notifyObservers("Animal at: " + animal.getPosition() + " skipped move");
//            System.out.println("Animal at: " + animal.getPosition() + " skipped move");
        }
        else{
            super.moveAnimal(animal);
        }
    }
}
