package kz.kaznu.classifier.genetic.creature;

import kz.kaznu.classifier.analyzer.AnalyzerType;
import kz.kaznu.classifier.analyzer.FilterType;
import kz.kaznu.classifier.fields.FieldsType;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/21/16
 */
public class Chromosome {
    private Gene[] genes;

    public Chromosome(Gene[] genes) {
        this.genes = genes;
    }


    public Gene[] getChromosomeArray() {
        return genes;
    }

    public void printGenes() {
        final StringBuilder output = new StringBuilder();
        for (Gene gene : genes) {
            int analyzer = gene.getAnalyzer();
            int classifier = gene.getClassifier();
            int fields = gene.getFields();
            int filter = gene.getFilter();
            output.append("\nanalyzer: ").append(AnalyzerType.values()[analyzer]).append("\t");
            output.append("filter: ").append(FilterType.values()[filter]).append("\t");
            output.append("classifier: ").append((classifier == 0 ? "SimpleBayes" : "KNN" + classifier)).append("\t");
            output.append("fields: ").append(FieldsType.values()[fields]).append("\n");
        }
        System.out.println(output.toString());
    }
}
