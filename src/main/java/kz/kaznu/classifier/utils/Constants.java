package kz.kaznu.classifier.utils;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/20/16
 */
public final class Constants {

    private Constants() {
    }

    public static String TMP_DIR = System.getProperty("java.io.tmpdir");
    public static String INDEX_FOLDER = TMP_DIR + "/lucene/";
    public static Integer NEW_POPULATION_SIZE = 8;
    public static int GENE_PARAMETERS_NUMBER = 4;
    public static int TOTAL_GENES_NUMBER = 5;
    public static String[] CLASSES = new String[]{"NOTIFICATION", "NEWS"};
    public static int MAXIMUM_KNN_K = 50;
    public static int CHANCE_TO_MUTATE = 35;
}
