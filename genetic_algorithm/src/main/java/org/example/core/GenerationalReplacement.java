package org.example.core;

import java.util.ArrayList;
import java.util.List;

public class GenerationalReplacement<T> implements ReplacementStrategy<T> {
    @Override
    public List<Chromosome<T>> replace(List<Chromosome<T>> currentPopulation, List<Chromosome<T>> offspringPopulation, int populationSize) {
        List<Chromosome<T>> newPopulation = new ArrayList<>(offspringPopulation);
        while (newPopulation.size() < populationSize) {
            newPopulation.add(currentPopulation.get(newPopulation.size() % currentPopulation.size()).copy());
        }
        return newPopulation.subList(0, populationSize);
    }
}