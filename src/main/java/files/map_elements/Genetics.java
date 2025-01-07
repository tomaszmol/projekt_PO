package files.map_elements;

import files.util.MapDirection;
import files.util.MoveDirection;

import java.util.Arrays;

public class Genetics {

    int[] genotype;
    int currentGenePosition;

    public Genetics(int geneNumber) {
        genotype = new int[geneNumber];
        Arrays.fill(genotype, 0);
        currentGenePosition = -1;
    }

    public MoveDirection getNextMoveInSequence() {
        currentGenePosition = (currentGenePosition + 1) % genotype.length;
        return MoveDirection.getMoveDirection( genotype[currentGenePosition] );
    }
    public MapDirection getNextTurnInSequence() {
        currentGenePosition = (currentGenePosition + 1) % genotype.length;
        return MapDirection.getMapDirection( genotype[currentGenePosition] );
    }

    public int[] getGenotype() {
        return genotype;
    }

    public void inheritGenesFromParents (Genetics parent1, double geneticContributionWeightParent1, Genetics parent2, double geneticContributionWeightParent2) {
        double geneProportionParent1 = (geneticContributionWeightParent1) / (geneticContributionWeightParent1 + geneticContributionWeightParent2);
        double geneProportionParent2 = (geneticContributionWeightParent2) / (geneticContributionWeightParent1 + geneticContributionWeightParent2);

        System.out.println(geneProportionParent1 + ", 2: " + geneProportionParent2);

        int[] genesParent1 = parent1.getGenotype();
        int[] genesParent2 = parent2.getGenotype();

        // generate father mother side 50/50
        if (Math.random() > 0.5) { // parent1 left side
            System.out.println("left side");
            for (int i = 0; i < genotype.length; i++) {
                if (geneProportionParent1 > (double) i / genotype.length) {
                    genotype[i] = genesParent1[i];
                } else {
                    genotype[i] = genesParent2[i];
                }
            }
        } else { // parent2 left side
            for (int i = 0; i < genotype.length; i++) {
                if (geneProportionParent2 > (double) i / genotype.length) {
                    genotype[i] = genesParent2[i];
                } else {
                    genotype[i] = genesParent1[i];
                }
            }
        }
    }

    public void mutateGeneticCode(double mutationChance) {
        for (int i = 0; i < genotype.length; i++) {
            if (Math.random() < mutationChance) {
                genotype[i] = (int) (Math.random() * 8);
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(genotype);
    }
}
