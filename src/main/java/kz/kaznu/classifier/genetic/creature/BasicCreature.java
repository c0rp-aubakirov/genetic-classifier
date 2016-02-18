package kz.kaznu.classifier.genetic.creature;

import kz.kaznu.classifier.CrossValidation;
import kz.kaznu.classifier.ValidationResult;
import kz.kaznu.classifier.analyzer.AnalyzerFactory;
import kz.kaznu.classifier.analyzer.AnalyzerType;
import kz.kaznu.classifier.analyzer.FilterType;
import kz.kaznu.classifier.classifier.ClassifierFactory;
import kz.kaznu.classifier.classifier.ClassifierWrapper;
import kz.kaznu.classifier.classifier.VotingClassifier;
import kz.kaznu.classifier.fields.FieldsFactory;
import kz.kaznu.classifier.fields.FieldsType;
import kz.kaznu.classifier.genetic.fitness.IFitness;
import kz.kaznu.classifier.utils.Constants;
import org.apache.lucene.classification.Classifier;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/21/16
 */
public class BasicCreature implements ICreature {
    private Chromosome chromosome;
    private Double fitnessValue;
    private IFitness fitness;
    private ValidationResult validationResult = null;
    private final List<Document> trainingSet;


    public BasicCreature(Chromosome chromosome, IFitness fitness, List<Document> trainingSet) {
        this.chromosome = chromosome;
        this.fitness = fitness;
        this.trainingSet = trainingSet;
    }

    @Override
    public Double calculateFitness() {
        final CrossValidation cv = new CrossValidation(10, Constants.CLASSES);
        final String type = "type";
        final List<ClassifierWrapper> classifiers = new ArrayList<>();

        for (Gene gene : chromosome.getChromosomeArray()) {
            final AnalyzerType analyzerType = AnalyzerType.values()[gene.getAnalyzer()];
            final FilterType filterType = FilterType.values()[gene.getFilter()];
            final Classifier classifier = ClassifierFactory.classifier(gene.getClassifier());
            final String[] field = FieldsFactory.field(FieldsType.values()[gene.getFields()]);
            classifiers.add(
                    new ClassifierWrapper(AnalyzerFactory.analyzer(analyzerType, filterType), classifier, field));
        }

        final VotingClassifier votingClassifier = new VotingClassifier(classifiers);
        try {
            validationResult = cv.validate(votingClassifier, getShuffled(2000), type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fitnessValue = fitness.accuracy(validationResult);
        return fitnessValue;
    }

    @Override
    public Chromosome getChromosome() {
        return chromosome;
    }

    @Override
    public IFitness getFitnessFunction() {
        return fitness;
    }

    @Override
    public Double getFitnessValue() {
        return fitnessValue;
    }

    @Override
    public ICreature newCreatureWith(Chromosome chromosome, IFitness fitness) {
        return new BasicCreature(chromosome, fitness, trainingSet);
    }

    @Override
    public ValidationResult getResult() {
        return validationResult;
    }

    private List<Document> getShuffled(final Integer n) {
        Collections.shuffle(trainingSet);
        return trainingSet.subList(0, n);
    }
}
