package org.example;

import org.example.caseStudy.MultiProjectFitness;
import org.example.core.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int numChromosomes = 5;
        int chromosomeLength = 5;

        double[] costs = {10, 20, 15, 25, 30};
        double[] profits = {40, 60, 30, 50, 70};
        double budget = 60;

        MultiProjectFitness fitness = new MultiProjectFitness(costs, profits, budget);
        Mutation bitFlip = new BitFlip();
        Mutation uniform = new Uniform();
        Mutation swap = new Swap();

        Crossover onePoint = new OnePointCrossover();
        Crossover uniformCross = new UniformCrossover();

        System.out.println("\n=== Integer Chromosomes with Swap Mutation ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            IntegerChromosome intChrom = new IntegerChromosome(chromosomeLength, 0, 9);
            System.out.println("Chromosome #" + i + " (before mutation): " + intChrom);
            swap.mutateFunction(intChrom, 0.8);
            System.out.println("Chromosome #" + i + " (after mutation):  " + intChrom);
            System.out.println("-----------------------------------\n");
        }

        System.out.println("=== Binary Chromosomes with BitFlip Mutation ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            Chromosome<Integer> binary = new BinaryChromosome(chromosomeLength);
            System.out.println("Chromosome #" + i + " (before mutation): " + binary);
            bitFlip.mutateFunction(binary, 0.5);
            System.out.println("Chromosome #" + i + " (after mutation):  " + binary);
            double score = fitness.evaluate(binary);
            System.out.println("Fitness of Chromosome #" + i + ": " + score + "\n-----------------------------------\n");
        }

        System.out.println("\n=== Floating-Point Chromosomes with Uniform Mutation ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            FloatingPointChromosome floatChrom = new FloatingPointChromosome(chromosomeLength, 0.0, 10.0);
            System.out.println("Chromosome #" + i + " (before mutation): " + floatChrom);
            uniform.mutateFunction(floatChrom, 0.5);
            System.out.println("Chromosome #" + i + " (after mutation):  " + floatChrom);
            System.out.println("-----------------------------------\n");
        }

        System.out.println("\n=== Binary Chromosomes with One-Point Crossover ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            BinaryChromosome parent1 = new BinaryChromosome(chromosomeLength);
            BinaryChromosome parent2 = new BinaryChromosome(chromosomeLength);
            System.out.println("Parent 1: " + parent1);
            System.out.println("Parent 2: " + parent2);
            Chromosome<Integer>[] offspring = onePoint.crossover(parent1, parent2, 1.0);
            System.out.println("Offspring 1: " + offspring[0]);
            System.out.println("Offspring 2: " + offspring[1]);
            System.out.println("-----------------------------------\n");
        }

        System.out.println("\n=== Binary Chromosomes with Uniform Crossover ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            BinaryChromosome p1 = new BinaryChromosome(chromosomeLength);
            BinaryChromosome p2 = new BinaryChromosome(chromosomeLength);
            System.out.println("Parent 1: " + p1);
            System.out.println("Parent 2: " + p2);
            Chromosome<Integer>[] off = uniformCross.crossover(p1, p2, 1.0);
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
            Chromosome<Integer>[] off = uniformCross.crossover(p1, p2, 1.0);
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
            Chromosome<Double>[] off = uniformCross.crossover(p1, p2, 1.0);
            System.out.println("Offspring 1: " + off[0]);
            System.out.println("Offspring 2: " + off[1]);
            System.out.println("-----------------------------------\n");
        }

        System.out.println("\n=== Binary Chromosomes with Two-Point Crossover ===\n");
        Crossover twoPoint = new TwoPointCrossover();
        for (int i = 1; i <= numChromosomes; i++) {
            BinaryChromosome parent1 = new BinaryChromosome(chromosomeLength);
            BinaryChromosome parent2 = new BinaryChromosome(chromosomeLength);
            System.out.println("Parent 1: " + parent1);
            System.out.println("Parent 2: " + parent2);
            Chromosome<Integer>[] offspring = twoPoint.crossover(parent1, parent2, 1.0);
            System.out.println("Offspring 1: " + offspring[0]);
            System.out.println("Offspring 2: " + offspring[1]);
            System.out.println("-----------------------------------\n");
        }

        System.out.println("\n=== Testing Replacement Strategies with Binary Chromosomes ===\n");
        List<Chromosome<Integer>> currentPopulation = new ArrayList<>();
        for (int i = 0; i < numChromosomes; i++) {
            BinaryChromosome chrom = new BinaryChromosome(chromosomeLength);
            chrom.setFitness(fitness.evaluate(chrom));
            currentPopulation.add(chrom);
        }
        System.out.println("Initial Population:");
        for (int i = 0; i < currentPopulation.size(); i++) {
            System.out.println("Chromosome #" + (i + 1) + ": " + currentPopulation.get(i) + ", Fitness: " + currentPopulation.get(i).getFitness());
        }

        List<Chromosome<Integer>> offspring = new ArrayList<>();
        for (int i = 0; i < numChromosomes / 2; i++) {
            BinaryChromosome parent1 = (BinaryChromosome) currentPopulation.get(i);
            BinaryChromosome parent2 = (BinaryChromosome) currentPopulation.get((i + 1) % numChromosomes);
            Chromosome<Integer>[] newOffspring = onePoint.crossover(parent1, parent2, 1.0);
            offspring.add(newOffspring[0]);
            offspring.add(newOffspring[1]);
            offspring.get(offspring.size() - 2).setFitness(fitness.evaluate(offspring.get(offspring.size() - 2)));
            offspring.get(offspring.size() - 1).setFitness(fitness.evaluate(offspring.get(offspring.size() - 1)));
        }
        System.out.println("\nGenerated Offspring:");
        for (int i = 0; i < offspring.size(); i++) {
            System.out.println("Offspring #" + (i + 1) + ": " + offspring.get(i) + ", Fitness: " + offspring.get(i).getFitness());
        }

        GenerationalReplacement<Integer> genReplace = new GenerationalReplacement<>();
        List<Chromosome<Integer>> genNextGen = genReplace.replace(currentPopulation, offspring, numChromosomes);
        System.out.println("\nAfter Generational Replacement:");
        for (int i = 0; i < genNextGen.size(); i++) {
            System.out.println("Chromosome #" + (i + 1) + ": " + genNextGen.get(i) + ", Fitness: " + genNextGen.get(i).getFitness());
        }

        SteadyStateReplacement<Integer> steadyReplace = new SteadyStateReplacement<>();
        List<Chromosome<Integer>> steadyNextGen = steadyReplace.replace(currentPopulation, offspring, numChromosomes);
        System.out.println("\nAfter Steady-State Replacement:");
        for (int i = 0; i < steadyNextGen.size(); i++) {
            System.out.println("Chromosome #" + (i + 1) + ": " + steadyNextGen.get(i) + ", Fitness: " + steadyNextGen.get(i).getFitness());
        }

        ElitismReplacement<Integer> elitismReplace = new ElitismReplacement<>(2);
        List<Chromosome<Integer>> elitismNextGen = elitismReplace.replace(currentPopulation, offspring, numChromosomes);
        System.out.println("\nAfter Elitism Replacement:");
        for (int i = 0; i < elitismNextGen.size(); i++) {
            System.out.println("Chromosome #" + (i + 1) + ": " + elitismNextGen.get(i) + ", Fitness: " + elitismNextGen.get(i).getFitness());
        }
    }
}