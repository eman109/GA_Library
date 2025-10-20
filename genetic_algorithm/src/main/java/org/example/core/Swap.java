package org.example;

import org.example.core.Chromosome;
import org.example.core.Mutation;
import org.example.core.IntegerChromosome;

public class Swap implements Mutation {

    @Override
    public void mutateFunction(Chromosome chromosome) {
        if (!(chromosome instanceof IntegerChromosome)) return;

        IntegerChromosome integerChromosome = (IntegerChromosome) chromosome;
        Integer[] genes = integerChromosome.getGenes();
        int length = genes.length;

        if (length <= 1) return;

        int ind1 = (int) (Math.random() * length);
        int ind2 = (int) (Math.random() * length);

        while (ind2 == ind1) {
            ind2 = (int) (Math.random() * length);
        }

        Integer temp = genes[ind1];
        genes[ind1] = genes[ind2];
        genes[ind2] = temp;

        integerChromosome.setGenes(genes);
    }
}
