package kz.kaznu.classifier.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import kz.kaznu.classifier.index.MessageToDocument;
import kz.moe.parser.model.CommonMessage;
import org.apache.lucene.document.Document;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/13/16
 */
public class ClassifierHelper {

    public static List<Document> readDocumentsFromFile() {
        final List<Document> empty = new ArrayList<>();
        final Gson gson = new GsonBuilder().create();
        final Type listType = new TypeToken<List<CommonMessage>>() {
        }.getType();
        final File file = new File("/tmp/news.json");
        try {
            if (file.exists()) {
                final FileReader fileReader = new FileReader(file);
                final JsonReader reader = new JsonReader(fileReader);
                List<CommonMessage> newsAndMessages = gson.fromJson(reader, listType);
                final List<Document> documents = newsAndMessages.stream()
                        .map(MessageToDocument::convert)
                        .collect(Collectors.toList());
                Collections.shuffle(documents);
                return documents;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return empty;
    }
}
