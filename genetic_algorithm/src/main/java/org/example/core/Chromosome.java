package org.example.core;

import java.util.Arrays;

public abstract class Chromosome {
    protected double[] genes;
    protected double fitness;

    public Chromosome(int length) {
        genes = new double[length];
    }

    public abstract void initialize();
    public abstract Chromosome copy();

    public int length() {
        return genes.length;
    }

    public double getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, double value) {
        genes[index] = value;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        return Arrays.toString(genes);
    }
}
