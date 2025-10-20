package org.example.core;

import java.util.Random;

public class OrderOneCrossover implements Crossover{

    private static final Random random = new Random();

    @Override
    public Chromosome[] crossover(Chromosome p1, Chromosome p2, double pc){

        int leng = p2.length();

        int r1 = 1 + random.nextInt(leng -2);
        int r2 = r1 + 1 + random.nextInt(leng - r1 - 1);
        double r3 = random.nextDouble();

        if(r3 > pc){
            return new Chromosome[]{p1.copy(), p2.copy()};
        }

        if(p1 instanceof IntegerChromosome && p2 instanceof IntegerChromosome){
            IntegerChromosome b1 = (IntegerChromosome)p1;
            IntegerChromosome b2 = (IntegerChromosome)p2;
            IntegerChromosome o1 = (IntegerChromosome)b1.copy();
            IntegerChromosome o2 = (IntegerChromosome)b2.copy();

            for(int i = r1; i < r2; i++){
                o1.setGene(i,((IntegerChromosome) p1).getGene(i));
                o2.setGene(i,((IntegerChromosome) p2).getGene(i));
            }

            restOfChromosome(o1,b2,r1,r2);
            restOfChromosome(o2,b1,r1,r2);

            return new Chromosome[]{o1, o2};

        }

        return new Chromosome[]{p1.copy(), p2.copy()};
    }

    private void restOfChromosome(IntegerChromosome offspring,IntegerChromosome p2,int r1 , int r2){
        int leng = offspring.length();

        boolean[] geneExists = new boolean[leng];

        for(int i = r1; i < r2; i++){
            geneExists[offspring.getGene(i)] = true;
        }

        int giver = 0;

        for(int i = 0; i < leng; i++){
            if(i >= r1 && i < r2) continue;

            while (geneExists[p2.getGene(giver)]) {
                giver++;
            }

            int insertGene = p2.getGene(giver);
            offspring.setGene(i,insertGene);
            geneExists[insertGene] = true;
            giver++;
        }
    }
}
