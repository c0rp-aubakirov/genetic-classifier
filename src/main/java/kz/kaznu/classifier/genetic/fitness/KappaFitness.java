package kz.kaznu.classifier.genetic.fitness;

import kz.kaznu.classifier.ValidationResult;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/21/16
 */
public class KappaFitness implements IFitness {
    @Override
    public Double accuracy(ValidationResult result) {
        return result.kappa;
    }
}
