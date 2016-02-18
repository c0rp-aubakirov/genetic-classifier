package kz.kaznu.classifier.genetic.crossover;


import kz.kaznu.classifier.genetic.creature.ICreature;

/**
 * User: Sanzhar Aubakirov
 * Date: 11/25/15
 */
public interface ICrossover {
    /**
     * Creates child using two parents. First parent is this, second parent is @another
     *
     * @param first  First parent of child
     * @param second Second parent of child
     * @return result of crossover between two ICreatures. The Child
     */
    ICreature with(ICreature first, ICreature second);
}
