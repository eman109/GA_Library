package org.example.core;
public interface InfeasibleHandler {
    boolean isFeasible(Chromosome chromosome);
    Chromosome repair(Chromosome chromosome);
}