package org.example.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElitismReplacement<T> implements ReplacementStrategy<T> {
    private final int eliteSize;

    public ElitismReplacement(int eliteSize) {
        this.eliteSize = eliteSize;
    }

    @Override
    public List<Chromosome<T>> replace(List<Chromosome<T>> currentPopulation, List<Chromosome<T>> offspringPopulation, int populationSize) {
        List<Chromosome<T>> combined = new ArrayList<>(currentPopulation);
        combined.addAll(offspringPopulation);
        Collections.sort(combined, (c1, c2) -> Double.compare(c2.getFitness(), c1.getFitness()));
        List<Chromosome<T>> newPopulation = new ArrayList<>();
        for (int i = 0; i < Math.min(eliteSize, combined.size()); i++) {
            newPopulation.add(combined.get(i).copy());
        }
        while (newPopulation.size() < populationSize) {
            newPopulation.add(combined.get(newPopulation.size() % combined.size()).copy());
        }
        return newPopulation.subList(0, populationSize);
    }
}