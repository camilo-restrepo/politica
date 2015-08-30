package co.inc.board.infrastructure.mongo;

import org.bson.Document;

public class MongoUtils {

    public static boolean documentIsBlank(Document targetDocument) {
        return (targetDocument == null) || (targetDocument.isEmpty());
    }
}
