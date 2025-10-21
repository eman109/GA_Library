package org.example.core;

import java.util.List;

public interface ReplacementStrategy<T> {
    List<Chromosome<T>> replace(List<Chromosome<T>> currentPopulation, List<Chromosome<T>> offspring, int populationSize);
}