package kz.kaznu.classifier.genetic.selection;

import kz.kaznu.classifier.genetic.creature.ICreature;
import kz.kaznu.classifier.genetic.crossover.ICrossover;

import java.util.List;

/**
 * User: Sanzhar Aubakirov
 * Date: 11/25/15
 */
public interface ISelection {

    /**
     * This function should select best part of population using some threshold
     * it should check if ICreature's fitnessValue more than some threshold
     * if yes => put it to the next population
     *
     * @return new population with creatures that fits our criteria
     */
    List<ICreature> with(List<ICreature> population, ICrossover crossover);
}
