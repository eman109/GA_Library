package org.example.core;

import java.util.Random;

public class IntegerChromosome extends Chromosome<Integer> {

    private static final Random random = new Random();
    private int minValue;
    private int maxValue;

    public IntegerChromosome(int length, int minValue, int maxValue) {
        genes = new Integer[length];
        this.minValue = minValue;
        this.maxValue = maxValue;
        initialize();
    }

    @Override
    public void initialize() {
        for (int i = 0; i < genes.length; i++) {
            genes[i] = random.nextInt(maxValue - minValue + 1) + minValue;
        }
    }

    @Override
    public Chromosome<Integer> copy() {
        IntegerChromosome clone = new IntegerChromosome(genes.length, minValue, maxValue);
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
