package org.example.caseStudy;



import org.example.ga.core.Chromosome;
import org.example.ga.core.InfeasibleHandler;
import org.example.ga.core.BinaryChromosome;
import java.util.ArrayList;
import java.util.List;

public class BudgetInfeasibleHandler implements InfeasibleHandler {
    private final double[] costs = {7000, 5000, 8000, 3000, 4000};
    private final double[] profits = {9000, 6000, 12000, 3500, 5000};
    private final double budget;

    public BudgetInfeasibleHandler(double budget) {
        this.budget = budget;
    }

    @Override
    public boolean isFeasible(Chromosome chromosome) {
        double totalCost = 0;
        for (int i = 0; i < chromosome.length(); i++) {
            if (chromosome.getGene(i) > 0.5) totalCost += costs[i];
        }
        return totalCost <= budget;
    }

    @Override
    public Chromosome repair(Chromosome chromosome) {
        if (!(chromosome instanceof BinaryChromosome)) return null;
        BinaryChromosome repaired = (BinaryChromosome) chromosome.copy();
        double totalCost = 0;
        List<Integer> funded = new ArrayList<>();
        for (int i = 0; i < repaired.length(); i++) {
            if (repaired.getGene(i) > 0.5) {
                totalCost += costs[i];
                funded.add(i);
            }
        }
        if (totalCost <= budget) return repaired;
        while (totalCost > budget && !funded.isEmpty()) {
            int worstIndex = funded.get(0);
            double worstRatio = profits[worstIndex] / costs[worstIndex];
            for (int idx : funded) {
                double ratio = profits[idx] / costs[idx];
                if (ratio < worstRatio) {
                    worstRatio = ratio;
                    worstIndex = idx;
                }
            }
            repaired.setGene(worstIndex, 0.0);
            funded.remove((Integer) worstIndex);
            totalCost = 0;
            for (int i = 0; i < repaired.length(); i++) {
                if (repaired.getGene(i) > 0.5) totalCost += costs[i];
            }
        }
        return totalCost <= budget ? repaired : null;
    }
}

