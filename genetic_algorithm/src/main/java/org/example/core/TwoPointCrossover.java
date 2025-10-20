package org.example.core;

import java.util.Random;

public class TwoPointCrossover implements Crossover{
    private static final Random random = new Random();

    @Override
    public Chromosome[] crossover(Chromosome p1, Chromosome p2, double pc) {
        int leng = p1.length();

        int r1 = 1 + random.nextInt(leng - 1);
        int r2 = r1 + 1 + random.nextInt(leng - r1 - 1);
        double r3 = random.nextDouble();

        if(r3 > pc){
            return new Chromosome[]{p1.copy(), p2.copy()};
        }

        //Binary
        if(p1 instanceof BinaryChromosome && p2 instanceof BinaryChromosome){
            BinaryChromosome b1 = (BinaryChromosome)p1;
            BinaryChromosome b2 = (BinaryChromosome)p2;
            BinaryChromosome o1 = (BinaryChromosome)b1.copy();
            BinaryChromosome o2 = (BinaryChromosome)b2.copy();

            for(int i = r1 ; i < r3 ; i++){
                int temp = o1.getGene(i);
                o1.setGene(i, o2.getGene(i));
                o2.setGene(i, temp);
            }
            return new Chromosome[]{o1, o2};
        }

        //Integer
        if(p1 instanceof IntegerChromosome && p2 instanceof IntegerChromosome){
            IntegerChromosome b1 = (IntegerChromosome) p1;
            IntegerChromosome b2 = (IntegerChromosome) p2;
            IntegerChromosome o1 = (IntegerChromosome) b1.copy();
            IntegerChromosome o2 = (IntegerChromosome) b2.copy();

            for(int i = r1 ; i < r3 ; i++){
                int temp = o1.getGene(i);
                o1.setGene(i, o2.getGene(i));
                o2.setGene(i, temp);
            }
            return new Chromosome[]{o1, o2};
        }

        //Floating point
        if(p1 instanceof FloatingPointChromosome && p2 instanceof FloatingPointChromosome){
            FloatingPointChromosome b1 = (FloatingPointChromosome)p1;
            FloatingPointChromosome b2 = (FloatingPointChromosome)p2;
            FloatingPointChromosome o1 = (FloatingPointChromosome)b1.copy();
            FloatingPointChromosome o2 = (FloatingPointChromosome)b2.copy();

            for(int i = r1; i < r2; i++){
                double temp = o1.getGene(i);
                o1.setGene(i, o2.getGene(i));
                o2.setGene(i, temp);
            }
            return new Chromosome[]{o1, o2};
        }
        return new Chromosome[]{p1.copy(), p2.copy()};
    }
}
