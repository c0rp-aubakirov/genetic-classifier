package kz.kaznu.classifier.classifier;

import kz.kaznu.classifier.index.MessageIndexer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.classification.ClassificationResult;
import org.apache.lucene.classification.Classifier;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class that helps to combine Classifiers in order to get majority vote to assign class
 */
public class VotingClassifier implements Classifier {

    private final List<ClassifierWrapper> classifiers;

    public VotingClassifier(List<ClassifierWrapper> classifiers) {
        this.classifiers = classifiers;
    }

    public void train(List<Document> trainingSet, String classFieldName) throws IOException {
        for (ClassifierWrapper wrapper : classifiers) {
            final MessageIndexer indexerTrain = new MessageIndexer(wrapper.getPathToTrainIndexFolder());
            indexerTrain.index(true, trainingSet, wrapper.analyzer());
            final IndexReader irTrain = indexerTrain.readIndex();
            final LeafReader wrap = SlowCompositeReaderWrapper.wrap(irTrain);
            wrapper.classifier()
                    .train(wrap, wrapper.fieldNames(), classFieldName, wrapper.analyzer(), new MatchAllDocsQuery());
        }
    }

    public void cleanUpIndexes() {
        for (ClassifierWrapper wrapper : classifiers) {
            FileUtils.deleteQuietly(new File(wrapper.getPathToTrainIndexFolder().replaceAll("/train", "")));
        }
    }

    public ClassificationResult<BytesRef> assignClass(Document document) throws IOException {
        final Map<String, Integer> assignedClasses = new HashMap<>();
        for (ClassifierWrapper classifier : classifiers) {
            final StringBuilder text = new StringBuilder();
            for (String fieldName : classifier.fieldNames()) {
                text.append(document.get(fieldName)).append(" ");
            }

            final ClassificationResult<BytesRef> classificationResult = classifier.classifier()
                    .assignClass(text.toString());
            final String assignedClass = classificationResult.getAssignedClass().utf8ToString();
            final int counter = Optional.ofNullable(assignedClasses.get(assignedClass)).orElse(0);
            assignedClasses.put(assignedClass, counter + 1);
        }

        String mostVoted = assignedClasses.keySet().stream().findFirst().get();
        int max = 0;
        for (Map.Entry<String, Integer> stringIntegerEntry : assignedClasses.entrySet()) {
            final Integer value = stringIntegerEntry.getValue();
            if (value > max) {
                max = value;
                mostVoted = stringIntegerEntry.getKey();
            }
        }
        return new ClassificationResult<>(new BytesRef(mostVoted.getBytes()), max);
    }


    @Override
    public ClassificationResult assignClass(String text) throws IOException {
        throw new NotImplementedException("These aren't the method you are looking for");
    }

    @Override
    public List<ClassificationResult> getClasses(String text) throws IOException {
        throw new NotImplementedException("These aren't the method you are looking for");
    }

    @Override
    public List<ClassificationResult> getClasses(String text, int max) throws IOException {
        throw new NotImplementedException("These aren't the method you are looking for");
    }

    @Override
    public void train(LeafReader leafReader, String textFieldName, String classFieldName,
                      Analyzer analyzer) throws IOException {
        throw new NotImplementedException("These aren't the method you are looking for");
    }

    @Override
    public void train(LeafReader leafReader, String textFieldName, String classFieldName, Analyzer analyzer,
                      Query query) throws IOException {
        throw new NotImplementedException("These aren't the method you are looking for");
    }

    @Override
    public void train(LeafReader leafReader, String[] textFieldNames, String classFieldName, Analyzer analyzer,
                      Query query) throws IOException {
        throw new NotImplementedException("These aren't the method you are looking for");
    }
}
