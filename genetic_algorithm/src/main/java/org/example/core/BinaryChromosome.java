package org.example.core;

import java.util.Random;

public class BinaryChromosome extends Chromosome {

    public BinaryChromosome(int length) {
        super(length);
        initialize();
    }

    @Override
    public void initialize() {
        Random random = new Random();
        for (int i = 0; i < genes.length; i++) {
            // assign either 0 or 1 randomly
            genes[i] = random.nextBoolean() ? 1.0 : 0.0;
        }
    }

    @Override
    public Chromosome copy() {

        BinaryChromosome clone = new BinaryChromosome(this.length());
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
            result += (int) genes[i];  // show as 0 or 1
            if (i < genes.length - 1) result += " ";
        }
        result += "]";
        return result;
    }
}
