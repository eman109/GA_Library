package org.example.core;


import java.util.Random;

public class IntegerChromosome extends Chromosome {

    private int minValue;
    private int maxValue;

    public IntegerChromosome(int length, int minValue, int maxValue) {
        super(length);
        this.minValue = minValue;
        this.maxValue = maxValue;
        initialize();
    }

    @Override
    public void initialize() {
        Random random = new Random();
        for (int i = 0; i < genes.length; i++) {
            // random integer between minValue and maxValue
            int value = random.nextInt(maxValue - minValue + 1) + minValue;
            genes[i] = value;
        }
    }

    @Override
    public Chromosome copy() {
        IntegerChromosome clone = new IntegerChromosome(this.length(), this.minValue, this.maxValue);
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
            result += (int) genes[i];
            if (i < genes.length - 1) result += " ";
        }
        result += "]";
        return result;
    }
}
