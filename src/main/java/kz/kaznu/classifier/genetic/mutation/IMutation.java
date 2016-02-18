package kz.kaznu.classifier.genetic.mutation;


import kz.kaznu.classifier.genetic.creature.ICreature;

/**
 * User: Sanzhar Aubakirov
 * Date: 11/25/15
 */
public interface IMutation {

    /**
     * Start mutation using one proposed algorithms
     * Choose one of this https://en.wikipedia.org/wiki/Mutation_(genetic_algorithm)
     */
    ICreature with(ICreature creature);
}
