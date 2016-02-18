package kz.kaznu.classifier.genetic.creature;

import kz.kaznu.classifier.utils.Constants;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/21/16
 */
public class Gene {
    private int classifier;
    private int analyzer;
    private int filter;
    private int fields;

    public Gene(int[] gens) {
        classifier = gens[0];
        analyzer = gens[1];
        filter = gens[2];
        fields = gens[3];
    }


    public int[] getChromosomeArray() {
        int[] gens = new int[Constants.GENE_PARAMETERS_NUMBER];
        gens[0] = classifier;
        gens[1] = analyzer;
        gens[2] = filter;
        gens[3] = fields;
        return gens;
    }

    public int getAnalyzer() {
        return analyzer;
    }

    public int getClassifier() {
        return classifier;
    }

    public int getFields() {
        return fields;
    }

    public int getFilter() {
        return filter;
    }
}
