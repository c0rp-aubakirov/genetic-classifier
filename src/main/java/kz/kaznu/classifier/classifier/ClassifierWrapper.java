package kz.kaznu.classifier.classifier;

import kz.kaznu.classifier.utils.Constants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.classification.Classifier;

import java.security.SecureRandom;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/20/16
 */
public class ClassifierWrapper {
    private final Classifier classifier;
    private final Analyzer analyzer;
    private final String[] fieldNames;
    private String pathToTrainIndexFolder;

    public ClassifierWrapper(Analyzer analyzer, Classifier classifier, String[] fieldNames) {
        this.analyzer = analyzer;
        this.classifier = classifier;
        this.fieldNames = fieldNames;

        final long random = new SecureRandom().nextInt(); // to process classifiers in parallel
        pathToTrainIndexFolder = Constants.INDEX_FOLDER + random + "/train";
    }

    public Analyzer analyzer() {
        return analyzer;
    }

    public Classifier classifier() {
        return classifier;
    }

    public String getPathToTrainIndexFolder() {
        return pathToTrainIndexFolder;
    }

    public String[] fieldNames() {
        return fieldNames;
    }
}
