package org.example.core;

import java.util.Random;

public class OnePointCrossover implements Crossover {
    private Random rand = new Random();

    @Override
    public Chromosome[] crossover(Chromosome p1, Chromosome p2) {
        int leng = p1.length();
        int r1 = 1 + rand.nextInt(leng - 1);

        //Binary
        if(p1 instanceof BinaryChromosome && p2 instanceof BinaryChromosome) {
            BinaryChromosome b1 = (BinaryChromosome) p1;
            BinaryChromosome b2 = (BinaryChromosome) p2;
            BinaryChromosome o1 = (BinaryChromosome) b1.copy();
            BinaryChromosome o2 = (BinaryChromosome) b2.copy();

            for(int i = r1; i < leng; i++) {
                boolean tmp = o1.getGene(i);
                o1.setGene(i, o2.getGene(i));
                o2.setGene(i,tmp);
            }
            return new Chromosome[]{o1, o2};
        }

        //Floating point
        if(p1 instanceof FloatingPointChromosome && p2 instanceof FloatingPointChromosome) {
            FloatingPointChromosome b1 = (FloatingPointChromosome) p1;
            FloatingPointChromosome b2 = (FloatingPointChromosome) p2;
            FloatingPointChromosome o1 = (FloatingPointChromosome) b1.copy();
            FloatingPointChromosome o2 = (FloatingPointChromosome) b2.copy();

            for(int i = r1; i < leng; i++) {
                double tmp = o1.getGene(i);
                o1.setGene(i, o2.getGene(i));
                o2.setGene(i,tmp);
            }
            return new Chromosome[]{o1, o2};
        }
        return new Chromosome[]{p1.copy(), p2.copy()};

    }

//    public static <T> void swap(T[] array, int i, int j) {
//        T temp = array[i];
//        array[i] = array[j];
//        array[j] = temp;
//    }

}