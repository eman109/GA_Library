package org.example.core;


import java.util.Random;

public class FloatingPointChromosome extends Chromosome {

    private double minValue;
    private double maxValue;

    public FloatingPointChromosome(int length, double minValue, double maxValue) {
        super(length);
        this.minValue = minValue;
        this.maxValue = maxValue;
        initialize();
    }

    @Override
    public void initialize() {
        Random random = new Random();
        for (int i = 0; i < genes.length; i++) {
            double value = minValue + (maxValue - minValue) * random.nextDouble();
            genes[i] = value;
        }
    }

    @Override
    public Chromosome copy() {
        FloatingPointChromosome clone = new FloatingPointChromosome(this.length(), this.minValue, this.maxValue);
        for (int i = 0; i < this.length(); i++) {
            clone.setGene(i, this.getGene(i));
        }
        clone.setFitness(this.getFitness());
        return clone;
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

