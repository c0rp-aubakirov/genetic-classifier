package kz.kaznu.classifier.fields;

/**
 * User: Sanzhar Aubakirov
 * Date: 1/21/16
 */
public class FieldsFactory {
    public static String[] field(FieldsType type) {
        switch (type) {
            case BODY:
                return new String[] {"body"};
//            case BODY_TITLE:
//                return new String[] {"body", "title"};
            case TITLE:
                return new String[] {"title"};
            default:
                return new String[] {"body"};
        }
    }
}
