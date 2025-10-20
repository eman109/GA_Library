package org.example.core;

import java.util.Random;

public class BitFlip implements Mutation {
    Random random = new Random();

    @Override
    public void mutateFunction(Chromosome chromosome) {
        if (!(chromosome instanceof BinaryChromosome)) return;

        BinaryChromosome binary = (BinaryChromosome) chromosome;
        Integer[] genes = binary.getGenes();
        int length = genes.length;
        if (length == 0) return;

        int index = (int) (Math.random() * length);
        if (genes[index] == 0)
            genes[index] = 1;
        else
            genes[index] = 0;

        binary.setGenes(genes);
    }
}
