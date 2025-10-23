package org.example.core;

import java.util.Random;

public class BitFlip implements Mutation {
    Random random = new Random();

    @Override
    public void mutateFunction(Chromosome chromosome , double PM ) {
        if (!(chromosome instanceof BinaryChromosome)){
            System.out.println("BitFlip mutation only works with BinaryChromosome. No mutation applied.");
            return;}
        BinaryChromosome binary = (BinaryChromosome) chromosome;
        Integer[] genes = binary.getGenes();
        int length = genes.length;
        if (length == 0) return;
        for (int i = 0; i < length; i++) {
            if (random.nextDouble() <= PM) {
                if (genes[i] == 0)
                    genes[i] = 1;
                else
                    genes[i] = 0;}}
        binary.setGenes(genes);
    }
}