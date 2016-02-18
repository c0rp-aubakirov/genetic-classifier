package kz.kaznu.classifier.genetic.selection;

import kz.kaznu.classifier.genetic.creature.ICreature;
import kz.kaznu.classifier.genetic.crossover.ICrossover;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import static kz.kaznu.classifier.utils.Constants.NEW_POPULATION_SIZE;

/**
 * User: Sanzhar Aubakirov
 * Date: 11/25/15
 */
public class RouletteSelection implements ISelection {
    private Logger logger = Logger.getLogger("ROULETTE_SELECTION");

    /**
     * Roulette selection is type of selection for GA
     * Read about it:
     * https://en.wikipedia.org/wiki/Fitness_proportionate_selection
     * http://www.obitko.com/tutorials/genetic-algorithms/selection.php
     *
     * @param population population to select
     * @param crossover  method of crossovering parents
     * @return new born population
     */
    @Override
    public List<ICreature> with(final List<ICreature> population, ICrossover crossover) {
        final List<ICreature> newPopulation = new ArrayList<>();

        final double sumFitness = population.stream().mapToDouble(ICreature::getFitnessValue).sum();

        while (newPopulation.size() < NEW_POPULATION_SIZE) {
            final int firstParentIndex = spinRoulette(population, sumFitness);
            final int secondParentIndex = spinRoulette(population, sumFitness);
            final ICreature child = crossover.with(population.get(firstParentIndex), population.get(secondParentIndex));
            newPopulation.add(child);
        }
        return newPopulation;
    }

    /**
     * Returns number of lucky creature, one of the future parent
     */
    private int spinRoulette(List<ICreature> population, double sumFitness) {
        final double randNumber = ThreadLocalRandom.current().nextDouble(0, sumFitness);
        double sum = 0.0;
        int i = 0;
        while (sum < randNumber) {
            sum = sum + population.get(i).getFitnessValue();
            i++;
        }
        return --i;
    }
}
