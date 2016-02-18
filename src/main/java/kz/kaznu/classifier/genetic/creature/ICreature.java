package kz.kaznu.classifier.genetic.creature;

import kz.kaznu.classifier.ValidationResult;
import kz.kaznu.classifier.genetic.fitness.IFitness;

/**
 * User: Sanzhar Aubakirov
 * Date: 11/25/15
 */
public interface ICreature {


    /**
     * This function should calculate fitness of this creature
     * to our task. As result returns some integer
     *
     * @return fitness of this creature to our task
     */
    Double calculateFitness();

    /**
     * This method returns creatures chromosome
     *
     * @return Chromosome of this instance
     */
    Chromosome getChromosome();

    /**
     * This method returns fitness object of this Creature
     * That is implemented IFitness
     *
     * @return IFitness of this creature
     */
    IFitness getFitnessFunction();


    /**
     * fitness shows how good is this creature.
     * It is calculated Purity or Confusion Matrix or smth else
     *
     * @return fitness value of this creature
     */
    Double getFitnessValue();

    /**
     * Should return new instance of this creature
     *
     * @param chromosome future chromosome
     * @param fitness    fitness function
     * @return new instance of this type of creature
     */
    ICreature newCreatureWith(Chromosome chromosome, IFitness fitness);


    /**
     * Should return validation result of classifier
     */
    ValidationResult getResult();
}
