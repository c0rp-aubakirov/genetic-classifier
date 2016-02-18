package kz.kaznu.classifier;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/21/16
 */
public class ValidationResult {
    private int[][] confusionMatrix;
    private String[] shortNameClasses;
    private StringBuilder readyToPrintResult = new StringBuilder();

    public final double observedAccuracy;
    public final double errorRate;
    public final double kappa;
    public final double expectedAccuracy;

    /**
     * Coefficients started from here make sense only for binary classifiers
     */
    public final double TP;
    public final double FP;
    public final double FN;
    public final double TN;

    public final double Precision;
    public final double Recall;

    public final double F1Score; // F Measure
    public final double MCC; // Matthews correlation coefficient or phi

    public ValidationResult(int[][] confusionMatrix, String[] shortNameClasses) {
        this.confusionMatrix = confusionMatrix;
        this.shortNameClasses = shortNameClasses;

        // build matrix for output
        final StringBuilder matrix = new StringBuilder();

        // add header
        matrix.append("\t\t");
        for (String clazz : shortNameClasses) {
            matrix.append(clazz).append("\t");
        }
        matrix.append("\n");

        int fc = 0, tc = 0, total = 0;
        for (int i = 0; i < confusionMatrix.length; i++) {
            matrix.append("\t").append(shortNameClasses[i]).append("\t");
            for (int j = 0; j < confusionMatrix.length; j++) {
                matrix.append(confusionMatrix[i][j]).append("\t");
                if (i == j) {
                    tc += confusionMatrix[i][j];
                } else {
                    fc += confusionMatrix[i][j];
                }
                total += confusionMatrix[i][j];
            }
            matrix.append("\n");
        }
        observedAccuracy = (double) tc / (double) (tc + fc);
        errorRate = (double) fc / (double) (tc + fc);

        /**
         * Calculate kappa
         * according to this http://stats.stackexchange.com/questions/82162/kappa-statistic-in-plain-english
         */
        final double[] forExpectedAccuracy = new double[confusionMatrix.length];
        for (int i = 0; i < forExpectedAccuracy.length; i++) {
            forExpectedAccuracy[i] = 1;
        }
        for (int k = 0; k < confusionMatrix.length; k++) {
            int wereClassified = 0; // instances were classified as CLASS by the machine learning classifier
            int wereLabeled = 0;    // instances were labeled as CLASS according to ground truth
            for (int i = 0; i < confusionMatrix.length; i++) {
                wereClassified += confusionMatrix[i][k];
            }
            for (int i = 0; i < confusionMatrix.length; i++) {
                wereLabeled += confusionMatrix[k][i];
            }
            forExpectedAccuracy[k] *= wereClassified * wereLabeled / total;
        }
        double sumAccuracy = 0;
        for (double classValue : forExpectedAccuracy) {
            sumAccuracy += classValue;
        }
        expectedAccuracy = sumAccuracy / total;

        kappa = (observedAccuracy - expectedAccuracy) / (1 - expectedAccuracy);

        readyToPrintResult
                .append("\n\tConfusion matrix:\n")
                .append(matrix.toString())
                .append("\n\tObserved Accuracy:\t")
                .append(observedAccuracy)
                .append("\n\tExpected Accuracy:\t")
                .append(expectedAccuracy)
                .append("\n\tError rate\t")
                .append(errorRate)
                .append("\n\tKappa statistics\t")
                .append(kappa);

        // ONLY if classifier is binary, has only TWO classes
        TP = confusionMatrix[0][0];
        FP = confusionMatrix[0][1];
        FN = confusionMatrix[1][0];
        TN = confusionMatrix[1][1];

        Precision = TP / (TP + FP);
        Recall = TP / (TP + FN);

        F1Score = (2 * TP) / (2 * TP + FP + FN); // F Measure
        MCC = ((TP * TN) - (FP * FN)) / Math.sqrt(
                (TP + FP) * (TP + FN) * (TN + FP) * (TN + FN)); // Matthews correlation coefficient or phi
        readyToPrintResult
                .append("\n\tPrecision\t")
                .append(Precision)
                .append("\n\tRecall\t")
                .append(Recall)
                .append("\n\tF measure\t")
                .append(F1Score)
                .append("\n\tMCC\t")
                .append(MCC);

        readyToPrintResult
                .append("\n\tTotal Docs #\t")
                .append(total)
                .append("\n==\t==\n\n");
    }


    public int[][] getConfusionMatrix() {
        return confusionMatrix;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public double getExpectedAccuracy() {
        return expectedAccuracy;
    }

    public double getF1Score() {
        return F1Score;
    }

    public double getFN() {
        return FN;
    }

    public double getFP() {
        return FP;
    }

    public double getKappa() {
        return kappa;
    }

    public double getMCC() {
        return MCC;
    }

    public double getObservedAccuracy() {
        return observedAccuracy;
    }

    public double getPrecision() {
        return Precision;
    }

    public StringBuilder getReadyToPrintResult() {
        return readyToPrintResult;
    }

    public double getRecall() {
        return Recall;
    }

    public String[] getShortNameClasses() {
        return shortNameClasses;
    }

    public double getTN() {
        return TN;
    }

    public double getTP() {
        return TP;
    }
}
