package org.example.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TournamentSelection implements Selection{
    private final int tournamentSize;
    private Random r=new Random();

    public TournamentSelection(int tournamentSize){
        this.tournamentSize=Math.max(2,tournamentSize);
    }

    @Override
    public Chromosome select(List<Chromosome> population) {
        List<Chromosome> tournament=new ArrayList<>(tournamentSize);
        int n= population.size();

        for(int i=0; i<tournamentSize; i++){
            int index=r.nextInt(n);
            tournament.add(population.get(index));
        }

        Chromosome best=tournament.get(0);
        for(Chromosome c: tournament){
            if(c.getFitness()> best.getFitness()){
                best=c;
            }
        }
        return best.copy();
    }
}