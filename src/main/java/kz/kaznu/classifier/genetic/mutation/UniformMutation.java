package kz.kaznu.classifier.genetic.mutation;


import kz.kaznu.classifier.analyzer.AnalyzerType;
import kz.kaznu.classifier.analyzer.FilterType;
import kz.kaznu.classifier.fields.FieldsType;
import kz.kaznu.classifier.genetic.creature.Chromosome;
import kz.kaznu.classifier.genetic.creature.Gene;
import kz.kaznu.classifier.genetic.creature.ICreature;

import java.util.concurrent.ThreadLocalRandom;

import static kz.kaznu.classifier.utils.Constants.*;

/**
 * User: Sanzhar Aubakirov
 * Date: 11/25/15
 */
public class UniformMutation implements IMutation {

    /**
     * This operator replaces the value of the chosen gene with a uniform
     * random value selected between the user-specified upper and lower
     * bounds for that gene. This mutation operator can only be used for integer and float genes.
     *
     * @param creature creature that will mutate
     * @return return new creature
     */
    @Override
    public ICreature with(final ICreature creature) {

        final Chromosome chromosome = creature.getChromosome();
        final Gene[] genes = chromosome.getChromosomeArray();

        final int mutationPercentage = CHANCE_TO_MUTATE; // 35 chance to mutate
        for (int i = 0; i < genes.length; i++) {
            if (ThreadLocalRandom.current().nextDouble(100) < mutationPercentage) {
                final int[] chromosomeArray = genes[i].getChromosomeArray();
                final Gene mutatedGene = mutateGene(chromosomeArray);
                genes[i] = mutatedGene;
            }
        }

        return creature.newCreatureWith(new Chromosome(genes), creature.getFitnessFunction());
    }

    private Gene mutateGene(int[] genes) {
        final int count = 2; // 50% gen mutation
        final boolean[] changedGen = new boolean[GENE_PARAMETERS_NUMBER];

        for (int i = 0; i < count; i++) {
            while (true) {
                final int randomIndex = ThreadLocalRandom.current().nextInt(0, GENE_PARAMETERS_NUMBER);
                if (!changedGen[randomIndex]) {
                    changedGen[randomIndex] = true;
                    genes[randomIndex] = randomMutation(randomIndex);
                    break;
                }
            }
        }
        return new Gene(genes);
    }

    private int randomMutation(final int genIndex) {

        // mutating K
        if (genIndex == 0) { // classifier mutation
            return ThreadLocalRandom.current().nextInt(0, MAXIMUM_KNN_K + 1);
        }
        if (genIndex == 1) { // analyzer
            return ThreadLocalRandom.current().nextInt(0, AnalyzerType.values().length);
        }
        if (genIndex == 2) { // filter
            return ThreadLocalRandom.current().nextInt(0, FilterType.values().length);
        }
        if (genIndex == 3) { // fields
            return ThreadLocalRandom.current().nextInt(0, FieldsType.values().length);
        }

        return 0;
    }
}