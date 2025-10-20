package org.example.core;

import java.util.Random;

public class BinaryChromosome extends Chromosome<Integer> {

    private static final Random random = new Random();

    public BinaryChromosome(int length) {
        genes = new Integer[length];
        initialize();
    }

    @Override
    public void initialize() {
        for (int i = 0; i < genes.length; i++) {
            genes[i] = random.nextInt(2); // 0 or 1
        }
    }

    @Override
    public Chromosome<Integer> copy() {
        BinaryChromosome clone = new BinaryChromosome(genes.length);
        for (int i = 0; i < genes.length; i++) {
            clone.genes[i] = this.genes[i];
        }
        clone.fitness = this.fitness;
        return clone;
    }

    @Override
    public int length() {
        return genes.length;
    }

    @Override
    public String toString() {
        String result = "[";
        for (int i = 0; i < genes.length; i++) {
            result += genes[i];
            if (i < genes.length - 1) result += " ";
        }
        result += "]";
        return result;
    }
}