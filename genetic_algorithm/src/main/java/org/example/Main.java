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

        Crossover onePoint = new OnePointCrossover();
        Crossover uniformCross = new UniformCrossover(); // <-- added UniformCrossover

        // ============================
        // Integer Chromosomes (Swap Mutation)
        // ============================
        System.out.println("\n=== Integer Chromosomes with Swap Mutation ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            IntegerChromosome intChrom = new IntegerChromosome(chromosomeLength, 0, 9);
            System.out.println("Chromosome #" + i + " (before mutation): " + intChrom);

            swap.mutateFunction(intChrom, 0.8); // higher mutation rate for swaps
            System.out.println("Chromosome #" + i + " (after mutation):  " + intChrom);
            System.out.println("-----------------------------------\n");
        }

        // ============================
        // Binary Chromosomes (BitFlip Mutation)
        // ============================
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

        // ============================
        // Floating-Point Chromosomes (Uniform Mutation)
        // ============================
        System.out.println("\n=== Floating-Point Chromosomes with Uniform Mutation ===\n");

        for (int i = 1; i <= numChromosomes; i++) {
            FloatingPointChromosome floatChrom = new FloatingPointChromosome(chromosomeLength, 0.0, 10.0);
            System.out.println("Chromosome #" + i + " (before mutation): " + floatChrom);

            // Apply Uniform mutation
            uniform.mutateFunction(floatChrom, 0.5); // same PM (0.5)
            System.out.println("Chromosome #" + i + " (after mutation):  " + floatChrom);
            System.out.println("-----------------------------------\n");
        }

        // ============================
        // Binary Chromosomes (One-Point Crossover)
        // ============================
        System.out.println("\n=== Binary Chromosomes with One-Point Crossover ===\n");

        for (int i = 1; i <= numChromosomes; i++) {
            BinaryChromosome parent1 = new BinaryChromosome(chromosomeLength);
            BinaryChromosome parent2 = new BinaryChromosome(chromosomeLength);

            System.out.println("Parent 1: " + parent1);
            System.out.println("Parent 2: " + parent2);

            Chromosome[] offspring = onePoint.crossover(parent1, parent2, 1.0); // 70% crossover probability

            System.out.println("Offspring 1: " + offspring[0]);
            System.out.println("Offspring 2: " + offspring[1]);
            System.out.println("-----------------------------------\n");
        }

        // ============================
        // Uniform Crossover demos (Binary, Integer, Floating)
        // ============================
        System.out.println("\n=== Binary Chromosomes with Uniform Crossover ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            BinaryChromosome p1 = new BinaryChromosome(chromosomeLength);
            BinaryChromosome p2 = new BinaryChromosome(chromosomeLength);

            System.out.println("Parent 1: " + p1);
            System.out.println("Parent 2: " + p2);

            Chromosome[] off = uniformCross.crossover(p1, p2, 1.0); // 80% chance to perform crossover
            System.out.println("Offspring 1: " + off[0]);
            System.out.println("Offspring 2: " + off[1]);
            System.out.println("-----------------------------------\n");
        }

        System.out.println("\n=== Integer Chromosomes with Uniform Crossover ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            IntegerChromosome p1 = new IntegerChromosome(chromosomeLength, 0, 9);
            IntegerChromosome p2 = new IntegerChromosome(chromosomeLength, 0, 9);

            System.out.println("Parent 1: " + p1);
            System.out.println("Parent 2: " + p2);

            Chromosome[] off = uniformCross.crossover(p1, p2, 1.0);
            System.out.println("Offspring 1: " + off[0]);
            System.out.println("Offspring 2: " + off[1]);
            System.out.println("-----------------------------------\n");
        }

        System.out.println("\n=== Floating-Point Chromosomes with Uniform Crossover ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            FloatingPointChromosome p1 = new FloatingPointChromosome(chromosomeLength, 0.0, 10.0);
            FloatingPointChromosome p2 = new FloatingPointChromosome(chromosomeLength, 0.0, 10.0);

            System.out.println("Parent 1: " + p1);
            System.out.println("Parent 2: " + p2);

            Chromosome[] off = uniformCross.crossover(p1, p2, 1.0);
            System.out.println("Offspring 1: " + off[0]);
            System.out.println("Offspring 2: " + off[1]);
            System.out.println("-----------------------------------\n");
        }

        // ============================
// Binary Chromosomes (Two-Point Crossover)
// ============================
        System.out.println("\n=== Binary Chromosomes with Two-Point Crossover ===\n");

        Crossover twoPoint = new TwoPointCrossover(); // ✅ Create instance

        for (int i = 1; i <= numChromosomes; i++) {
            BinaryChromosome parent1 = new BinaryChromosome(chromosomeLength);
            BinaryChromosome parent2 = new BinaryChromosome(chromosomeLength);

            System.out.println("Parent 1: " + parent1);
            System.out.println("Parent 2: " + parent2);

            Chromosome[] offspring = twoPoint.crossover(parent1, parent2, 1.0); // 70% probability

            System.out.println("Offspring 1: " + offspring[0]);
            System.out.println("Offspring 2: " + offspring[1]);
            System.out.println("-----------------------------------\n");
        }

    }
}
