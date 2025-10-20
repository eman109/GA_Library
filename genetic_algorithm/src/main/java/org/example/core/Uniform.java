package org.example.core;

import org.example.core.Chromosome;
import org.example.core.Mutation;
import org.example.core.FloatingPointChromosome;
import java.util.Random;

public class Uniform implements Mutation {

    Random random = new Random();

    @Override
    public void mutateFunction(Chromosome chromosome, double PM) {
        if (!(chromosome instanceof FloatingPointChromosome)) return;

        FloatingPointChromosome floatChrom = (FloatingPointChromosome) chromosome;
        Double[] genes = floatChrom.getGenes();
        double[] lowerBounds = getLowerBounds(genes.length, floatChrom);
        double[] upperBounds = getUpperBounds(genes.length, floatChrom);


        for (int i = 0; i < genes.length; i++) {
            double randVal = random.nextDouble();

            if (randVal < PM) {

                double xi = genes[i];
                double LBi = lowerBounds[i];
                double UBi = upperBounds[i];

                double deltaL = xi - LBi;
                double deltaU = UBi - xi;

                double r1 = random.nextDouble();

                double delta;
                boolean goLeft;

                if (r1 <= 0.5) {
                    delta = deltaL;
                    goLeft = true;
                } else {
                    delta = deltaU;
                    goLeft = false;
                }

                double r2 = random.nextDouble() * delta;

                double newXi;
                if (goLeft)
                    newXi = xi - r2;
                else
                    newXi = xi + r2;

                if (newXi < LBi) newXi = LBi;
                if (newXi > UBi) newXi = UBi;

                genes[i] = newXi;
            }
        }

        floatChrom.setGenes(genes);
    }

    // These simulate lower/upper bounds from FloatingPointChromosome
    private double[] getLowerBounds(int length, FloatingPointChromosome chrom) {
        double[] lb = new double[length];
        for (int i = 0; i < length; i++) lb[i] = chrom.getMinValue();
        return lb;
    }

    private double[] getUpperBounds(int length, FloatingPointChromosome chrom) {
        double[] ub = new double[length];
        for (int i = 0; i < length; i++) ub[i] = chrom.getMaxValue();
        return ub;
    }
}
