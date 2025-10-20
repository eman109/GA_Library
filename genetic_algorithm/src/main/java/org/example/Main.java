package org.example;

import org.example.caseStudy.MultiProjectFitness;
import org.example.core.BinaryChromosome;
import org.example.core.Chromosome;
import org.example.core.Mutation;
import org.example.core.BitFlip;

public class Main {
    public static void main(String[] args) {

        int numChromosomes = 5; // number of chromosomes to generate
        int chromosomeLength = 5;

        double[] costs = {10, 20, 15, 25, 30};
        double[] profits = {40, 60, 30, 50, 70};
        double budget = 60;

        MultiProjectFitness fitness = new MultiProjectFitness(costs, profits, budget);
        Mutation bitFlip = new BitFlip();

        System.out.println("=== Multi-Chromosome Evaluation ===\n");

        for (int i = 1; i <= numChromosomes; i++) {
            Chromosome binary = new BinaryChromosome(chromosomeLength);
            System.out.println("Chromosome #" + i + " (before mutation): " + binary);

            // Apply BitFlip mutation
            bitFlip.mutateFunction(binary);
            System.out.println("Chromosome #" + i + " (after mutation):  " + binary);

            // Evaluate fitness
            double score = fitness.evaluate(binary);
            System.out.println("Fitness of Chromosome #" + i + ": " + score + "\n-----------------------------------\n");
        }
    }
}
