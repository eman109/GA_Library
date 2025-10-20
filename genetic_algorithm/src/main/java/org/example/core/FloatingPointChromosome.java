package org.example.core;

import java.util.Random;

public class FloatingPointChromosome extends Chromosome<Double> {

    private static final Random random = new Random();
    private double minValue;
    private double maxValue;

    public FloatingPointChromosome(int length, double minValue, double maxValue) {
        genes = new Double[length];
        this.minValue = minValue;
        this.maxValue = maxValue;
        initialize();
    }

    @Override
    public void initialize() {
        for (int i = 0; i < genes.length; i++) {
            genes[i] = minValue + (maxValue - minValue) * random.nextDouble();
        }
    }

    @Override
    public Chromosome<Double> copy() {
        FloatingPointChromosome clone = new FloatingPointChromosome(genes.length, minValue, maxValue);
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
            result += String.format("%.2f", genes[i]);
            if (i < genes.length - 1) result += " ";
        }
        result += "]";
        return result;
    }
}
