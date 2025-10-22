package org.example.core;

import java.util.List;
import java.util.Random;

public class RouletteSelection implements Selection {
    @Override
    public Chromosome select(List<Chromosome> population) {
        double totalFitness=0;
        for(Chromosome c:population){
            totalFitness +=c.getFitness();
        }
        double r=Math.random()*totalFitness;

        double partialSum=0;
        for(Chromosome c:population){
            partialSum+=c.getFitness();
            if(partialSum>r){
                return c;
            }
        }
        return population.get(population.size()-1);
    }
}
