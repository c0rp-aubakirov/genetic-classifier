package kz.kaznu.classifier.classifier;

import org.apache.lucene.classification.Classifier;
import org.apache.lucene.classification.KNearestNeighborClassifier;
import org.apache.lucene.classification.SimpleNaiveBayesClassifier;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/21/16
 */
public class ClassifierFactory {

    /**
     * We assume that 0 is SimpleNaiveBayesClassifier
     * 1-200 is argument for KNearestNeighborClassifier which is representing K - number of neighbors
     * if < 0 or > 200 return
     * @param type integer that represents classifier
     * @return classifier
     */
    public static Classifier classifier(final int type) {
        if (isBetween(type, 1, 200)) {
            return new KNearestNeighborClassifier(type);
        }
//        if (type == -1) {
//            return new BooleanPerceptronClassifier();
//        }
        return new SimpleNaiveBayesClassifier();
    }

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }
}
