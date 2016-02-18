package kz.kaznu.classifier.genetic.fitness;


import kz.kaznu.classifier.ValidationResult;

/**
 * User: Sanzhar Aubakirov
 * Date: 11/25/15
 */
public interface IFitness {
    /**
     * Function that evaluates accuracy of classification.
     * Should return float value between 0 to 1 [0,1]
     *
     * @param result cross validation result
     * @return calculated accuracy related to trainingCluster
     */

    Double accuracy(ValidationResult result);
}
