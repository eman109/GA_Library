package org.example;

import org.example.caseStudy.MultiProjectFitness;
import org.example.core.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        int numChromosomes = 5;
        int chromosomeLength = 5;

        double[] costs = {50, 70, 120, 80, 60}; double[] profits = {100, 150, 300, 160, 90}; double budget = 200;

        MultiProjectFitness fitness = new MultiProjectFitness(costs, profits, budget);

        // Mutations
        Mutation bitFlip = new BitFlip();
        Mutation uniform = new Uniform();
        Mutation swap = new Swap();

        // Crossovers
        Crossover onePoint = new OnePointCrossover();
        Crossover uniformCross = new UniformCrossover();
        Crossover twoPoint = new TwoPointCrossover();

        // ============================
        // Integer Chromosomes (Swap Mutation)
        // ============================
        System.out.println("\n=== Integer Chromosomes with Swap Mutation ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            IntegerChromosome intChrom = new IntegerChromosome(chromosomeLength, 0, 9);
            System.out.println("Chromosome #" + i + " (before mutation): " + intChrom);
            swap.mutateFunction(intChrom, 0.8);
            System.out.println("Chromosome #" + i + " (after mutation):  " + intChrom);
            System.out.println("-----------------------------------\n");
        }

        // ============================
        // Binary Chromosomes (BitFlip Mutation)
        // ============================
        System.out.println("=== Binary Chromosomes with BitFlip Mutation ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            BinaryChromosome binary = new BinaryChromosome(chromosomeLength);
            System.out.println("Chromosome #" + i + " (before mutation): " + binary);
            bitFlip.mutateFunction(binary, 0.5);
            System.out.println("Chromosome #" + i + " (after mutation):  " + binary);
            double score = fitness.evaluate(binary);
            System.out.println("Fitness of Chromosome #" + i + ": " + score);
            System.out.println("-----------------------------------\n");
        }

        // ============================
        // Floating-Point Chromosomes (Uniform Mutation)
        // ============================
        System.out.println("=== Floating-Point Chromosomes with Uniform Mutation ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            FloatingPointChromosome floatChrom = new FloatingPointChromosome(chromosomeLength, 0.0, 10.0);
            System.out.println("Chromosome #" + i + " (before mutation): " + floatChrom);
            uniform.mutateFunction(floatChrom, 0.5);
            System.out.println("Chromosome #" + i + " (after mutation):  " + floatChrom);
            System.out.println("-----------------------------------\n");
        }

        // ============================
        // Crossover Demos
        // ============================
        System.out.println("=== Binary Chromosomes with One-Point Crossover ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            BinaryChromosome parent1 = new BinaryChromosome(chromosomeLength);
            BinaryChromosome parent2 = new BinaryChromosome(chromosomeLength);
            System.out.println("Parent 1: " + parent1);
            System.out.println("Parent 2: " + parent2);
            Chromosome[] offspring = onePoint.crossover(parent1, parent2, 1.0);
            System.out.println("Offspring 1: " + offspring[0]);
            System.out.println("Offspring 2: " + offspring[1]);
            System.out.println("-----------------------------------\n");
        }

        System.out.println("=== Uniform Crossover (All Types) ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            BinaryChromosome p1 = new BinaryChromosome(chromosomeLength);
            BinaryChromosome p2 = new BinaryChromosome(chromosomeLength);
            System.out.println("Parent 1: " + p1);
            System.out.println("Parent 2: " + p2);
            Chromosome[] off = uniformCross.crossover(p1, p2, 1.0);
            System.out.println("Offspring 1: " + off[0]);
            System.out.println("Offspring 2: " + off[1]);
            System.out.println("-----------------------------------\n");
        }

        // ============================
        // Two-Point Crossover
        // ============================
        System.out.println("=== Binary Chromosomes with Two-Point Crossover ===\n");
        for (int i = 1; i <= numChromosomes; i++) {
            BinaryChromosome parent1 = new BinaryChromosome(chromosomeLength);
            BinaryChromosome parent2 = new BinaryChromosome(chromosomeLength);
            System.out.println("Parent 1: " + parent1);
            System.out.println("Parent 2: " + parent2);
            Chromosome[] offspring = twoPoint.crossover(parent1, parent2, 1.0);
            System.out.println("Offspring 1: " + offspring[0]);
            System.out.println("Offspring 2: " + offspring[1]);
            System.out.println("-----------------------------------\n");
        }

        // ============================
        // Selection Demo
        // ============================
        System.out.println("\n=== Selection Demo ===\n");
        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < numChromosomes; i++) {
            BinaryChromosome c = new BinaryChromosome(chromosomeLength);
            c.setFitness(fitness.evaluate(c));
            population.add(c);
        }

        Selection roulette = new RouletteSelection();
        System.out.println("--- Roulette Selection ---");
        for (int i = 1; i <= numChromosomes; i++) {
            Chromosome selected = roulette.select(population);
            System.out.println("Selected Chromosome #" + i + ": " + selected + " | Fitness: " + selected.getFitness());
        }

        Selection tournament = new TournamentSelection(2);
        System.out.println("\n--- Tournament Selection ---");
        for (int i = 1; i <= numChromosomes; i++) {
            Chromosome selected = tournament.select(population);
            System.out.println("Selected Chromosome #" + i + ": " + selected + " | Fitness: " + selected.getFitness());
        }

        // ============================
        // Replacement Strategies Demo
        // ============================
        System.out.println("\n=== Testing Replacement Strategies with Binary Chromosomes ===\n");

        List<Chromosome<Integer>> currentPopulation = new ArrayList<>();
        for (int i = 0; i < numChromosomes; i++) {
            BinaryChromosome chrom = new BinaryChromosome(chromosomeLength);
            chrom.setFitness(fitness.evaluate(chrom));
            currentPopulation.add(chrom);
        }

        System.out.println("Initial Population:");
        for (int i = 0; i < currentPopulation.size(); i++) {
            System.out.println("Chromosome #" + (i + 1) + ": " + currentPopulation.get(i)
                    + ", Fitness: " + currentPopulation.get(i).getFitness());
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
            System.out.println("Offspring #" + (i + 1) + ": " + offspring.get(i)
                    + ", Fitness: " + offspring.get(i).getFitness());
        }

        GenerationalReplacement<Integer> genReplace = new GenerationalReplacement<>();
        List<Chromosome<Integer>> genNextGen = genReplace.replace(currentPopulation, offspring, numChromosomes);
        System.out.println("\nAfter Generational Replacement:");
        for (int i = 0; i < genNextGen.size(); i++) {
            System.out.println("Chromosome #" + (i + 1) + ": " + genNextGen.get(i)
                    + ", Fitness: " + genNextGen.get(i).getFitness());
        }

        SteadyStateReplacement<Integer> steadyReplace = new SteadyStateReplacement<>();
        List<Chromosome<Integer>> steadyNextGen = steadyReplace.replace(currentPopulation, offspring, numChromosomes);
        System.out.println("\nAfter Steady-State Replacement:");
        for (int i = 0; i < steadyNextGen.size(); i++) {
            System.out.println("Chromosome #" + (i + 1) + ": " + steadyNextGen.get(i)
                    + ", Fitness: " + steadyNextGen.get(i).getFitness());
        }

        ElitismReplacement<Integer> elitismReplace = new ElitismReplacement<>(2);
        List<Chromosome<Integer>> elitismNextGen = elitismReplace.replace(currentPopulation, offspring, numChromosomes);
        System.out.println("\nAfter Elitism Replacement:");
        for (int i = 0; i < elitismNextGen.size(); i++) {
            System.out.println("Chromosome #" + (i + 1) + ": " + elitismNextGen.get(i)
                    + ", Fitness: " + elitismNextGen.get(i).getFitness());
        }
    }
}
