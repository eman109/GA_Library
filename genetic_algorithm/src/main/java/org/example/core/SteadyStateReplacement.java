package org.example.core;

import java.util.ArrayList;
import java.util.List;

public class SteadyStateReplacement<T> implements ReplacementStrategy<T> {
    @Override
    public List<Chromosome<T>> replace(List<Chromosome<T>> currentPopulation, List<Chromosome<T>> offspringPopulation, int populationSize) {
        List<Chromosome<T>> newPopulation = new ArrayList<>(currentPopulation);
        for (Chromosome<T> offspring : offspringPopulation) {
            int worstIndex = getWorstIndex(newPopulation);
            if (worstIndex >= 0 && worstIndex < newPopulation.size()) {
                newPopulation.set(worstIndex, offspring.copy());
            }
        }
        return newPopulation;
    }

    private int getWorstIndex(List<Chromosome<T>> population) {
        if (population.isEmpty()) return -1;
        int worstIndex = 0;
        for (int i = 1; i < population.size(); i++) {
            if (population.get(i).getFitness() < population.get(worstIndex).getFitness()) {
                worstIndex = i;
            }
        }
        return worstIndex;
    }
}