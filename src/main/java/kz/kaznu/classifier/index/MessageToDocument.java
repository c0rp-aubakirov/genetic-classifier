package kz.kaznu.classifier.index;

import kz.moe.parser.model.AMessage;
import kz.moe.parser.model.MessageType;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/5/16
 */
public class MessageToDocument {
    /**
     * Converts Abstract Message to Lucene Document
     *
     * @return resulted document
     */
    public static Document convert(AMessage message) {
        return createWith(message.getTitle(), message.getBody(), message.getType());
    }

    /**
     * Creates Lucene Document using two strings
     *
     * @return resulted document
     */
    public static Document createWith(String titleStr, String bodyStr, MessageType msgClass) {
        final Document document = new Document();

        final FieldType textIndexedType = new FieldType();
        textIndexedType.setStored(true);
        textIndexedType.setStoreTermVectors(true);
        textIndexedType.setStoreTermVectorOffsets(true);
        textIndexedType.setStoreTermVectorPositions(true);
        textIndexedType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        textIndexedType.setTokenized(true);

        final FieldType typeField = new FieldType();
        typeField.setTokenized(false);
        typeField.setStored(true);

        //index title
        Field title = new Field("title", titleStr, textIndexedType);
        //index body
        Field body = new Field("body", bodyStr, textIndexedType);
        //index type
        Field type = new Field("type", msgClass.name(), typeField);

        document.add(title);
        document.add(body);
        document.add(type);
        return document;
    }
}
