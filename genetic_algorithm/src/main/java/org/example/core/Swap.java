
package org.example.core;
import org.example.core.Chromosome;
import org.example.core.Mutation;
import org.example.core.IntegerChromosome;
import java.util.Random;
public class Swap implements Mutation {
    Random random = new Random();
    @Override
    public void mutateFunction(Chromosome chromosome  ,double PM) {
        if (!(chromosome instanceof IntegerChromosome)) return;
        IntegerChromosome integerChromosome = (IntegerChromosome) chromosome;
        Integer[] genes = integerChromosome.getGenes();
        int length = genes.length;
        double randval = random.nextDouble();
        if (length <= 1) return;
        if (randval >= PM) return;
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