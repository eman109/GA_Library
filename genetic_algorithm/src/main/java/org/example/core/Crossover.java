package org.example.core;
import org.example.core.Chromosome;

public interface Crossover {
    Chromosome[] crossover(Chromosome p1, Chromosome p2 , double pc);
}