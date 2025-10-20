package org.example.core;

public abstract class Chromosome<T> {
    protected T[] genes;
    protected double fitness;

    public abstract void initialize();
    public abstract Chromosome<T> copy();
    public abstract int length();
    @Override
    public abstract String toString();

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public T getGene(int index) {
        return genes[index];
    }

    public void setGene(int index, T value) {
        genes[index] = value;
    }

    public T[] getGenes() {
        return genes;
    }

    public void setGenes(T[] genes) {
        this.genes = genes;
    }

}
