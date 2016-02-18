package kz.kaznu.classifier.genetic.crossover;


import kz.kaznu.classifier.genetic.creature.Chromosome;
import kz.kaznu.classifier.genetic.creature.Gene;
import kz.kaznu.classifier.genetic.creature.ICreature;

import java.util.concurrent.ThreadLocalRandom;

import static kz.kaznu.classifier.utils.Constants.TOTAL_GENES_NUMBER;

/**
 * User: Sanzhar Aubakirov
 * Date: 11/25/15
 */
public class OnePointCrossover implements ICrossover {

    @Override
    public ICreature with(ICreature first, ICreature second) {

        final Chromosome chromosomeMother = first.getChromosome();
        final Chromosome chromosomeFather = second.getChromosome();

        final int random = ThreadLocalRandom.current().nextInt(0, TOTAL_GENES_NUMBER);

        final Gene[] gens1 = chromosomeMother.getChromosomeArray();
        final Gene[] gens2 = chromosomeFather.getChromosomeArray();

        final Gene[] gens = new Gene[TOTAL_GENES_NUMBER];

        System.arraycopy(gens1, 0, gens, 0, random);

        System.arraycopy(gens2, random, gens, random, TOTAL_GENES_NUMBER - (random));

        return first.newCreatureWith(new Chromosome(gens), first.getFitnessFunction());
    }
}
