package org.example.caseStudy;



import  org.example.ga.core.Chromosome;
import org.example.ga.core.FitnessFunction;

public class MultiProjectFitness implements FitnessFunction {
    private final double[] costs = {7000, 5000, 8000, 3000, 4000};
    private final double[] profits = {9000, 6000, 12000, 3500, 5000};
    private final double budget;

    public MultiProjectFitness(double budget) {
        this.budget = budget;
    }

    @Override
    public double evaluate(Chromosome c) {
        double totalCost = 0;
        double totalProfit = 0;
        for (int i = 0; i < c.length(); i++) {
            if (c.getGene(i) > 0.5) {
                totalCost += costs[i];
                totalProfit += profits[i];
            }
        }
        if (totalCost == 0) return 0;
        double efficiency = totalProfit / totalCost;
        if (totalCost > budget) {
            double penalty = budget / totalCost;
            return (totalProfit * penalty) * 0.5;
        } else {
            return totalProfit * efficiency;
        }
    }
}
