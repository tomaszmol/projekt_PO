package tests;

import files.util.MoveDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import files.map_elements.*;
import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.*;

class GeneticsTest {

    private Genetics genetics;

    @BeforeEach
    void setUp() {
        genetics = new Genetics(10); // Przyjmijmy, że genotyp ma 10 genów
    }

    @Test
    void constructorShouldInitializeGenotypeWithZeros() {
        int[] expectedGenotype = new int[10];
        Arrays.fill(expectedGenotype, 0);

        assertArrayEquals(expectedGenotype, genetics.getGenotype());
    }

    @Test
    void getNextMoveInSequenceShouldReturnForward() {
        assertEquals(MoveDirection.FORWARD, genetics.getNextMoveInSequence());
    }

    @Test
    void inheritGenesFromParentsShouldCombineGenesCorrectly() {
        Genetics parent1 = new Genetics(10);
        Genetics parent2 = new Genetics(10);

        int[] parent1Genes = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        int[] parent2Genes = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

        System.arraycopy(parent1Genes, 0, parent1.getGenotype(), 0, 10);
        System.arraycopy(parent2Genes, 0, parent2.getGenotype(), 0, 10);

        genetics.inheritGenesFromParents(parent1, 0.6, parent2, 0.4);

        assertNotNull(genetics.getGenotype());

        int[] possibleInherited = {2,2,2,2,1,1,1,1,1,1};
        int[] possibleInherited2 = {1,1,1,1,1,1,2,2,2,2};

        System.out.println(Arrays.toString(possibleInherited));
        System.out.println(Arrays.toString(possibleInherited2));
        System.out.println(Arrays.toString(genetics.getGenotype()));

        assertTrue(Arrays.equals(genetics.getGenotype(), possibleInherited) || Arrays.equals(genetics.getGenotype(), possibleInherited2));
    }

    @Test
    void mutateGeneticCodeShouldChangeGenesWithChance() {
        Arrays.fill(genetics.getGenotype(),-1); // outside gene pool
        genetics.mutateGeneticCode(10,10); // 100% szansa na mutację
        int[] mutatedGenes = genetics.getGenotype();
        assertTrue(Arrays.stream(mutatedGenes).allMatch(g -> g != -1)); // wszystkie zmutowane
    }
}