package org.example;

import org.example.caseStudy.MultiProjectFitness;
import org.example.core.*;

public class Main {
    public static void main(String[] args) {

        int numChromosomes = 5; // number of chromosomes to generate
        int chromosomeLength = 5;

        double[] costs = {10, 20, 15, 25, 30};
        double[] profits = {40, 60, 30, 50, 70};
        double budget = 60;

        MultiProjectFitness fitness = new MultiProjectFitness(costs, profits, budget);
        Mutation bitFlip = new BitFlip();
        Mutation uniform = new Uniform();
        Mutation swap = new Swap();

        System.out.println("\n=== Integer Chromosomes with Swap Mutation ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            IntegerChromosome intChrom = new IntegerChromosome(chromosomeLength, 0, 9);
            System.out.println("Chromosome #" + i + " (before mutation): " + intChrom);

            swap.mutateFunction(intChrom, 0.8); // higher mutation rate for swaps
            System.out.println("Chromosome #" + i + " (after mutation):  " + intChrom);
            System.out.println("-----------------------------------\n");
        }

        System.out.println("=== Binary Chromosomes with BitFlip Mutation ===\n");

        for (int i = 1; i <= numChromosomes; i++) {
            Chromosome binary = new BinaryChromosome(chromosomeLength);
            System.out.println("Chromosome #" + i + " (before mutation): " + binary);

            // Apply BitFlip mutation
            bitFlip.mutateFunction(binary, 0.5); // pass mutation probability (e.g., 0.5)
            System.out.println("Chromosome #" + i + " (after mutation):  " + binary);

            // Evaluate fitness
            double score = fitness.evaluate(binary);
            System.out.println("Fitness of Chromosome #" + i + ": " + score + "\n-----------------------------------\n");
        }

        System.out.println("\n=== Floating-Point Chromosomes with Uniform Mutation ===\n");

        for (int i = 1; i <= numChromosomes; i++) {
            FloatingPointChromosome floatChrom = new FloatingPointChromosome(chromosomeLength, 0.0, 10.0);
            System.out.println("Chromosome #" + i + " (before mutation): " + floatChrom);

            // Apply Uniform mutation
            uniform.mutateFunction(floatChrom, 0.5); // same PM (0.5)
            System.out.println("Chromosome #" + i + " (after mutation):  " + floatChrom);

            // Evaluate fitness — optional, only if MultiProjectFitness supports floating-point genes
            System.out.println("-----------------------------------\n");
        }
    }
}
