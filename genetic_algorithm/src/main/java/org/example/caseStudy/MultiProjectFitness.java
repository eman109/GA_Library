package org.example.caseStudy;

import org.example.core.Chromosome;
import org.example.core.FitnessFunction;

public class MultiProjectFitness implements FitnessFunction {
    private final double[] costs;
    private final double[] profits;
    private final double budget;

    public MultiProjectFitness(double[] costs, double[] profits, double budget) {
        this.costs = costs;
        this.profits = profits;
        this.budget = budget;
    }

    @Override
    public double evaluate(Chromosome c) {
        double totalCost = 0;
        double totalProfit = 0;

        System.out.println("\n--- Fitness Evaluation Details ---");
        for (int i = 0; i < c.length(); i++) {
            Integer gene = (Integer) c.getGene(i);
            System.out.print("Gene " + i + " = " + gene);
            if (gene == 1) {
                totalCost += costs[i];
                totalProfit += profits[i];
                System.out.println(" -> Included: cost=" + costs[i] + ", profit=" + profits[i]);
            } else {
                System.out.println(" -> Not included");
            }
        }

        System.out.println("Total Cost = " + totalCost);
        System.out.println("Total Profit = " + totalProfit);

        if (totalCost == 0) {
            System.out.println("Fitness = 0 (no projects selected)");
            return 0;
        }

        double efficiency = totalProfit / totalCost;

        if (totalCost > budget) {
            // Make over-budget fitness always smaller than minimum feasible fitness
            double fitnessScore = totalProfit * efficiency * 0.1; // reduce drastically
            System.out.println("Over budget! Heavy penalty applied.");
            System.out.println("Fitness = " + fitnessScore);
            return fitnessScore;
        } else {
            double fitnessScore = totalProfit * efficiency;
            System.out.println("Within budget.");
            System.out.println("Efficiency = " + efficiency);
            System.out.println("Fitness = " + fitnessScore);
            return fitnessScore;
        }
    }
}
