package org.example.core;

import java.util.List;

public interface Selection {
    Chromosome select(List<Chromosome> population);
}