package kz.kaznu.classifier;

import kz.kaznu.classifier.analyzer.AnalyzerType;
import kz.kaznu.classifier.analyzer.FilterType;
import kz.kaznu.classifier.fields.FieldsType;
import kz.kaznu.classifier.genetic.creature.BasicCreature;
import kz.kaznu.classifier.genetic.creature.Chromosome;
import kz.kaznu.classifier.genetic.creature.Gene;
import kz.kaznu.classifier.genetic.creature.ICreature;
import kz.kaznu.classifier.genetic.crossover.ICrossover;
import kz.kaznu.classifier.genetic.crossover.OnePointCrossover;
import kz.kaznu.classifier.genetic.fitness.IFitness;
import kz.kaznu.classifier.genetic.fitness.KappaFitness;
import kz.kaznu.classifier.genetic.mutation.IMutation;
import kz.kaznu.classifier.genetic.mutation.UniformMutation;
import kz.kaznu.classifier.genetic.selection.ISelection;
import kz.kaznu.classifier.genetic.selection.RouletteSelection;
import kz.kaznu.classifier.utils.ClassifierHelper;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static kz.kaznu.classifier.utils.Constants.*;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/21/16
 */
public class GeneticMain {
    private static ICreature maximum = null;

//    static {
//        final Settings settings = Settings.settingsBuilder().put("cluster.name", "my-application").build();
//        try {
//            client = TransportClient.builder().settings(settings).build().addTransportAddress(
//                    new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//    }

    private final static List<Document> newsAndNotifications = ClassifierHelper.readDocumentsFromFile();


    public static void main(String[] args) throws IOException {

        final IFitness fitness = new KappaFitness();
        final ISelection selection = new RouletteSelection();
        final IMutation mutation = new UniformMutation();
        final ICrossover crossover = new OnePointCrossover();

        final List<ICreature> creatures = new ArrayList<>();
        for (int i = 0; i < NEW_POPULATION_SIZE; i++) {
            Chromosome e = generateRandomChromosome();
            creatures.add(new BasicCreature(e, fitness, newsAndNotifications));
        }
        List<ICreature> nextGen = oneIteration(selection, mutation, crossover, creatures);
        while (true) {
            nextGen = oneIteration(selection, mutation, crossover, nextGen);
        }
    }

    /**
     * One genetic algorithm iteration. It finished when fitness value is >0.95
     *
     * @param selection selection method to use
     * @param mutation mutation method to use
     * @param crossover crossover method to use
     * @param creatures generation that will do everything
     * @return return next generation.
     * @throws IOException
     */
    private static List<ICreature> oneIteration(ISelection selection, IMutation mutation, ICrossover crossover,
                                                List<ICreature> creatures) throws IOException {
        creatures.parallelStream().forEach(creature -> {
            final Double fitnessValue = creature.calculateFitness();
            if (fitnessValue > 0.95) {
                Double average = 0D;
                for (int i = 0; i < 7; i++) {
                    average += creature.calculateFitness();
                }

                if (average / 7 > 0.95) {
                    creature.getChromosome().printGenes();
                    System.out.println(creature.getResult().getReadyToPrintResult().toString());
                    System.exit(1);
                } else {
                    System.out.println("Found solution not reliable");
                    maximum = null;
                }
            }
        });

        boolean maximumChanged = false;
        for (ICreature creature : creatures) {
            if (maximum == null || maximum.getFitnessValue() < creature.getFitnessValue()) {
                maximum = creature;
                maximumChanged = true;
            }
        }
        if (maximumChanged) {
            System.out.println("maximum fitness\t" + maximum.getFitnessValue());
            final Double nextFitness = maximum.calculateFitness();
            System.out.println("next fitness\t" + nextFitness);
            System.out.println(maximum.getResult().getReadyToPrintResult().toString());
            maximum.getChromosome().printGenes();
            if (maximum.getFitnessValue() - 0.1 >= nextFitness) {
                maximum = null;
            }
        }

        final List<ICreature> survived = selection.with(creatures, crossover);
        final List<ICreature> nextGen = new ArrayList<>();
        for (ICreature creature : survived) {
            if (new SecureRandom().nextInt(100) > 70) {
                nextGen.add(mutation.with(creature));
            } else {
                nextGen.add(creature);
            }
        }

        return nextGen;
    }

    private static Chromosome generateRandomChromosome() {
        final Gene[] genes = new Gene[TOTAL_GENES_NUMBER];

        for (int i = 0; i < genes.length; i++) {
            final int[] gene = new int[GENE_PARAMETERS_NUMBER];
            for (int j = 0; j < gene.length; j++) {
                gene[j] = generateRandomGeneWithIndex(j);
            }
            genes[i] = new Gene(gene);
        }
        return new Chromosome(genes);
    }

    /**
     * Generates gene value depending on gene index
     *
     * @param index index of gene in Chromosome
     * @return random int
     */
    private static int generateRandomGeneWithIndex(final int index) {
        final Random random = new SecureRandom();
        switch (index) {
            case 0:
                return random.nextInt(MAXIMUM_KNN_K + 1); // classifier
            case 1:
                return random.nextInt(AnalyzerType.values().length);
            case 2:
                return random.nextInt(FilterType.values().length);
            case 3:
                return random.nextInt(FieldsType.values().length);
            default:
                throw new RuntimeException();
        }
    }
}
