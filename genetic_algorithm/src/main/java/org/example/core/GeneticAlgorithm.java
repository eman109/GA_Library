package org.example.core;

import java.util.*;

public class GeneticAlgorithm<T> {

    private int populationSize;
    private int generations;
    private double crossoverRate;
    private double mutationRate;
    private int geneLength;
    private int chromType; // 1 = Binary, 2 = Integer, 3 = Floating

    private FitnessFunction fitness;
    private InfeasibleHandler infeasibleHandler;
    private Crossover crossover;
    private Mutation mutation;
    private Selection selection;
    private ReplacementStrategy<T> replacementStrategy;

    public GeneticAlgorithm(int populationSize, int generations, double crossoverRate, double mutationRate,
                            int geneLength, int chromType,
                            FitnessFunction fitness,
                            InfeasibleHandler infeasibleHandler,
                            Crossover crossover, Mutation mutation, Selection selection,
                            ReplacementStrategy<T> replacementStrategy) {

        this.populationSize = populationSize;
        this.generations = generations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.geneLength = geneLength;
        this.chromType = chromType;
        this.fitness = fitness;
        this.infeasibleHandler = infeasibleHandler;
        this.crossover = crossover;
        this.mutation = mutation;
        this.selection = selection;
        this.replacementStrategy = replacementStrategy;
    }

    @SuppressWarnings("unchecked")
    public Chromosome<T> run() {
        Random rand = new Random();

        // === Step 1: Initialize Population ===
        List<Chromosome<T>> population = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            Chromosome<?> c;

            switch (chromType) {
                case 1 -> c = new BinaryChromosome(geneLength);
                case 2 -> c = new IntegerChromosome(geneLength, 0, 9);
                case 3 -> c = new FloatingPointChromosome(geneLength, 0.0, 1.0);
                default -> c = new BinaryChromosome(geneLength);
            }

            while (!infeasibleHandler.isFeasible(c)) {
                switch (chromType) {
                    case 1 -> c = new BinaryChromosome(geneLength);
                    case 2 -> c = new IntegerChromosome(geneLength, 0, 9);
                    case 3 -> c = new FloatingPointChromosome(geneLength, 0.0, 1.0);
                }
            }

            c.setFitness(fitness.evaluate(c));
            population.add((Chromosome<T>) c);
        }

        // === Step 2: Main GA Loop ===
        for (int gen = 0; gen < generations; gen++) {
            List<Chromosome<T>> offspring = new ArrayList<>();

            while (offspring.size() < populationSize) {
                Chromosome<T> parent1 = (Chromosome<T>) selection.select((List) population).copy();
                Chromosome<T> parent2 = (Chromosome<T>) selection.select((List) population).copy();

                Chromosome<?>[] children = crossover.crossover(parent1, parent2, crossoverRate);

                for (Chromosome<?> child : children) {
                    mutation.mutateFunction(child, mutationRate);

                    if (!infeasibleHandler.isFeasible(child))
                        child = infeasibleHandler.repair(child);

                    child.setFitness(fitness.evaluate(child));
                    offspring.add((Chromosome<T>) child);

                    if (offspring.size() >= populationSize)
                        break;
                }
            }

            // === Step 3: Replacement ===
            population = replacementStrategy.replace(population, offspring, populationSize);

            // === Step 4: Track Best Chromosome ===
            Chromosome<T> best = population.get(0);
            for (Chromosome<T> c : population)
                if (c.getFitness() > best.getFitness())
                    best = c;

            System.out.println("Generation " + gen + " Best Fitness: " + best.getFitness());
        }

        // === Step 5: Return Best Chromosome from Last Generation ===
        Chromosome<T> bestLastGen = population.get(0);
        for (Chromosome<T> c : population)
            if (c.getFitness() > bestLastGen.getFitness())
                bestLastGen = c;

        System.out.println("=== Final Best Chromosome (Last Generation) ===");
        System.out.println("Genes: " + bestLastGen);
        System.out.println("Fitness: " + bestLastGen.getFitness());

        return bestLastGen;
    }
}
