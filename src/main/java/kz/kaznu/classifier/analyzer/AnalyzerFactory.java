package kz.kaznu.classifier.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/19/16
 */
public class AnalyzerFactory {

    public static Analyzer analyzer(AnalyzerType type, FilterType filter) {
        final CustomizableRussianAnalyzer analyzer = new CustomizableRussianAnalyzer();

        switch (filter) {
            case STEMMING_AND_REMOVE_SHORT:
                analyzer.ifNeedStemming(true).ifNeedRemoveShort(true);
                break;
            case STEMMING_AND_NOT_REMOVE_SHORT:
                analyzer.ifNeedStemming(true).ifNeedRemoveShort(false);
                break;
            case NO_STEMMING_AND_REMOVE_SHORT:
                analyzer.ifNeedStemming(false).ifNeedRemoveShort(true);
                break;
            case NO_STEMMING_AND_NOT_REMOVE_SHORT:
                analyzer.ifNeedStemming(false).ifNeedRemoveShort(false);
                break;
        }

        switch (type) {
//            case ALL_UP_TO_FIVE_GRAMS:
//                return new ShingleAnalyzerWrapper(analyzer, 2, 5, " ", true, true, "_");
//            case FIVE_GRAM:
//                return new ShingleAnalyzerWrapper(analyzer, 5, 5, " ", false, true, "_");
            case FOUR_GRAM:
                return new ShingleAnalyzerWrapper(analyzer, 4, 4, " ", false, true, "_");
            case THREE_GRAM:
                return new ShingleAnalyzerWrapper(analyzer, 3, 3, " ", false, true, "_");
            case TWO_GRAM:
                return new ShingleAnalyzerWrapper(analyzer, 2, 2, " ", false, true, "_");
            case UNIGRAM:
                return analyzer;
            default:
                return analyzer;
        }

    }
}
