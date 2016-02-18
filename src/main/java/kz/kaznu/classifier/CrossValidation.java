package kz.kaznu.classifier;

import kz.kaznu.classifier.classifier.ClassifierWrapper;
import kz.kaznu.classifier.classifier.VotingClassifier;
import kz.kaznu.classifier.index.MessageIndexer;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.classification.ClassificationResult;
import org.apache.lucene.classification.Classifier;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Logger;

import static kz.kaznu.classifier.utils.Constants.INDEX_FOLDER;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/13/16
 */
public class CrossValidation {
    private final Logger logger = Logger.getLogger("CLASSIFIER");
    /**
     * It is for K-Fold cross-validation
     * CROSS_VALIDATION_SETS_NUMBER is a number of subsets
     */
    private final int CROSS_VALIDATION_SETS_NUMBER;
    private int[][] confusionMatrix;
    private Map<String, Integer> typeIndex;
    private String[] classes;
    private String[] shortNameClasses;

    /**
     * During K-Fold cross-validation you divide all set into K subsets
     *
     * @param crossValidationSetsNumber is a number of subsets for Cross Validation
     * @param classes                   Array of classes to detect
     */
    public CrossValidation(final int crossValidationSetsNumber, final String[] classes) {
        this.CROSS_VALIDATION_SETS_NUMBER = crossValidationSetsNumber;
        this.classes = classes;
        initShortNames(classes);
    }

    /**
     * Start Classifier training and testing using K-Fold cross-validation
     *
     * @param wrapper        Pair of Classifier and Analyzer that will be used for train and validation
     * @param documents      list of documents that will be used for training and testing wrapper
     * @param fieldNames     array of fields that will be used by wrapper. Field should exists in Document
     * @param classFieldName the name of the field containing the class assigned to documents
     * @throws IOException If there is a low-level I/O error.
     */
    public ValidationResult validate(final ClassifierWrapper wrapper,
                                     final List<Document> documents,
                                     final String[] fieldNames,
                                     final String classFieldName) throws IOException {
        final StringBuilder output = new StringBuilder();

        initConfusionMatrix(classes); // refresh matrix
        Collections.shuffle(documents); // shuffle documents
        final List<List<Document>> set = getCrossValidationSets(documents);

        // K-Fold cross-validation
        for (int i = 0; i < CROSS_VALIDATION_SETS_NUMBER; i++) {

            // Create TrainingSet and index it
            final List<Document> trainingSet = new ArrayList<>();
            for (int k = 0; k < CROSS_VALIDATION_SETS_NUMBER; k++) {
                if (k == i) continue; // do not include test set to training
                trainingSet.addAll(set.get(k));
            }

            final long random = new SecureRandom().nextInt(); // to process classifiers in parallel
            trainClassifier(wrapper, trainingSet, fieldNames, classFieldName, random);

            // Create TestingSet and index it
            final List<Document> testingSet = new ArrayList<>();
            testingSet.addAll(set.get(i));

            checkClassifier(testingSet, wrapper.classifier(), fieldNames, classFieldName);
            FileUtils.deleteQuietly(new File(INDEX_FOLDER + random));
        }

        return new ValidationResult(confusionMatrix, shortNameClasses);
    }

    /**
     * Start Classifier training and testing using K-Fold cross-validation
     *
     * @param classifier     instance of voting classifier
     * @param documents      list of documents that will be used for training and testing classifier
     * @param classFieldName the name of the field containing the class assigned to documents
     * @throws IOException If there is a low-level I/O error.
     */
    public ValidationResult validate(final VotingClassifier classifier,
                                     final List<Document> documents,
                                     final String classFieldName) throws IOException {
        final StringBuilder output = new StringBuilder();

        initConfusionMatrix(classes); // refresh matrix
        Collections.shuffle(documents); // shuffle documents
        final List<List<Document>> set = getCrossValidationSets(documents);

        // K-Fold cross-validation
        for (int i = 0; i < CROSS_VALIDATION_SETS_NUMBER; i++) {

            // Create TrainingSet and index it
            final List<Document> trainingSet = new ArrayList<>();
            for (int k = 0; k < CROSS_VALIDATION_SETS_NUMBER; k++) {
                if (k == i) continue; // do not include test set to training
                trainingSet.addAll(set.get(k));
            }

            classifier.train(trainingSet, classFieldName);

            // Create TestingSet and index it
            final List<Document> testingSet = new ArrayList<>();
            testingSet.addAll(set.get(i));

            checkClassifier(testingSet, classifier, classFieldName);

            classifier.cleanUpIndexes();

            final ValidationResult tempResult = new ValidationResult(confusionMatrix, shortNameClasses);
            if(tempResult.kappa < 0.7) return tempResult;
        }

        return new ValidationResult(confusionMatrix, shortNameClasses);
    }

    /**
     * Start Classifier training and testing using K-Fold cross-validation
     *
     * @param wrapper        Pair of Classifier and Analyzer that will be used for train and validation
     * @param documents      list of documents that will be used for training and testing wrapper
     * @param fieldNames     array of fields that will be used by wrapper. Field should exists in Document
     * @param classFieldName the name of the field containing the class assigned to documents
     * @throws IOException If there is a low-level I/O error.
     */
    public ValidationResult learningCurveCrossValidation(final ClassifierWrapper wrapper,
                                                         final List<Document> documents,
                                                         final String[] fieldNames,
                                                         final String classFieldName) throws IOException {
        initConfusionMatrix(classes); // refresh matrix
        Collections.shuffle(documents); // shuffle documents
        final List<List<Document>> set = getCrossValidationSets(documents);

        // K-Fold cross-validation
        for (int i = 0; i < CROSS_VALIDATION_SETS_NUMBER; i++) {
            // Create TestingSet and index it
            final List<Document> testingSet = new ArrayList<>();
            testingSet.addAll(set.get(i));

            checkClassifier(testingSet, wrapper.classifier(), fieldNames, classFieldName);
        }

        return new ValidationResult(confusionMatrix, shortNameClasses);
    }


    /**
     * Start Classifier training and testing using K-Fold cross-validation
     *
     * @param wrapper        Pair of Classifier and Analyzer that will be used for train and validation
     * @param documents      list of documents that will be used for training and testing wrapper
     * @param fieldNames     array of fields that will be used by wrapper. Field should exists in Document
     * @param classFieldName the name of the field containing the class assigned to documents
     * @throws IOException If there is a low-level I/O error.
     */
    public ValidationResult learningCurveSimpleValidation(final ClassifierWrapper wrapper,
                                                          final List<Document> documents,
                                                          final String[] fieldNames,
                                                          final String classFieldName) throws IOException {

        initConfusionMatrix(classes); // refresh matrix
        Collections.shuffle(documents); // shuffle documents

        final long random = new SecureRandom().nextInt(); // to process classifiers in parallel
        trainClassifier(wrapper, documents, fieldNames, classFieldName, random);

        checkClassifier(documents, wrapper.classifier(), fieldNames, classFieldName);
        FileUtils.deleteQuietly(new File(INDEX_FOLDER + random));

        return new ValidationResult(confusionMatrix, shortNameClasses);
    }

    private void trainClassifier(ClassifierWrapper wrapper, List<Document> documents, String[] fieldNames,
                                 String classFieldName, long random) throws IOException {
        final String pathToTrainIndexFolder = INDEX_FOLDER + random + "/train";
        final MessageIndexer indexerTrain = new MessageIndexer(pathToTrainIndexFolder);
        indexerTrain.index(true, documents, wrapper.analyzer());
        final IndexReader irTrain = indexerTrain.readIndex();
        final LeafReader wrap = SlowCompositeReaderWrapper.wrap(irTrain);
        if (fieldNames.length == 1) { //TODO its for PerceptronClassifier, it doesn't work with multiple fields
            wrapper.classifier()
                    .train(wrap, fieldNames[0], classFieldName, wrapper.analyzer(), new MatchAllDocsQuery());

        } else {
            wrapper.classifier()
                    .train(wrap, fieldNames, classFieldName, wrapper.analyzer(), new MatchAllDocsQuery());
        }
    }

    /**
     * This is for K-Fold cross-validation
     * <p>
     * Divide list of all documents into K sets
     * K = CROSS_VALIDATION_SETS_NUMBER
     */
    private List<List<Document>> getCrossValidationSets(final List<Document> documents) {
        final int TOTAL_SET_SIZE = documents.size();
        final int EACH_SUBSET_SIZE = TOTAL_SET_SIZE / CROSS_VALIDATION_SETS_NUMBER;

        final List<List<Document>> set = new ArrayList<>(CROSS_VALIDATION_SETS_NUMBER);
        for (int i = 0; i < CROSS_VALIDATION_SETS_NUMBER; i++) {
            final List<Document> setItem = new ArrayList<>(EACH_SUBSET_SIZE);
            for (int j = i * EACH_SUBSET_SIZE; j < (i + 1) * EACH_SUBSET_SIZE; j++) {
                final Document document = documents.get(j);
                setItem.add(document);
            }
            set.add(setItem);
        }
        return set;
    }

//    http://soleami.com/blog/comparing-document-classification-functions-of-lucene-and-mahout.html

    /**
     * This method classifying testing documents and accumulates confusion matrix
     *
     * @param testingSet     validation set
     * @param classifier     trained classifier
     * @param fieldNames     array of fields that will be used by classifier. Field should exists in Document
     * @param classFieldName the name of the field containing the class assigned to documents
     * @throws IOException If there is a low-level I/O error.
     */
    private void checkClassifier(final List<Document> testingSet, final Classifier classifier,
                                 final String[] fieldNames, final String classFieldName) throws IOException {

        // init local confusion matrix
        final int[][] tempConfusionMatrix = new int[classes.length][classes.length];

        for (Document document : testingSet) {
            final String correctAnswer = document.get(classFieldName);
            final int cai = typeIndex.get(correctAnswer);

            final StringBuilder text = new StringBuilder();
            for (String fieldName : fieldNames) {
                text.append(document.get(fieldName)).append(" ");
            }
            final ClassificationResult result = classifier.assignClass(text.toString());
            final String classified = getClassFromObject(result.getAssignedClass());
            final int cli = typeIndex.get(classified);
            tempConfusionMatrix[cai][cli]++;
        }

        for (int i = 0; i < tempConfusionMatrix.length; i++) {
            for (int j = 0; j < tempConfusionMatrix.length; j++) {
                confusionMatrix[i][j] += tempConfusionMatrix[i][j];
            }
        }
    }

    /**
     * This method classifying testing documents and accumulates confusion matrix
     *
     * @param testingSet     validation set
     * @param classifier     trained classifier
     * @param classFieldName the name of the field containing the class assigned to documents
     * @throws IOException If there is a low-level I/O error.
     */
    private void checkClassifier(final List<Document> testingSet,
                                 final VotingClassifier classifier,
                                 final String classFieldName) throws IOException {

        // init local confusion matrix
        final int[][] tempConfusionMatrix = new int[classes.length][classes.length];

        for (Document document : testingSet) {
            final String correctAnswer = document.get(classFieldName);
            final int cai = typeIndex.get(correctAnswer);

            final ClassificationResult<BytesRef> result = classifier.assignClass(document);
            final String classified = result.getAssignedClass().utf8ToString();
            final int cli = typeIndex.get(classified);
            tempConfusionMatrix[cai][cli]++;
        }

        for (int i = 0; i < tempConfusionMatrix.length; i++) {
            for (int j = 0; j < tempConfusionMatrix.length; j++) {
                confusionMatrix[i][j] += tempConfusionMatrix[i][j];
            }
        }
    }

    private void initConfusionMatrix(final String[] classes) {
        confusionMatrix = new int[classes.length][classes.length];
        typeIndex = new HashMap<>();
        for (int i = 0; i < classes.length; i++) {
            typeIndex.put(classes[i], i);
        }
    }

    /**
     * We shorten class names. Otherwise confusion matrix output may looks stretched
     */
    private void initShortNames(String[] classes) {
        shortNameClasses = new String[classes.length];
        for (int i = 0; i < classes.length; i++) {
            final String shortName = "C" + i;
            shortNameClasses[i] = shortName;
        }
    }

    public String getClassFromObject(Object object) {
        if (object instanceof BytesRef) {
            return ((BytesRef) object).utf8ToString();
        }

        if (object instanceof Boolean) {
            return ((Boolean) object) ? classes[0] : classes[1];
        }

        throw new RuntimeException("You implemented new classifier?");
    }
}
