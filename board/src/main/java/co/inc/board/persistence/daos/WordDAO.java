package co.inc.board.persistence.daos;

import co.inc.board.domain.entities.CloudTag;
import co.inc.board.domain.entities.TwitterTarget;
import co.inc.board.domain.entities.WordCount;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class WordDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordDAO.class);

    public static final String WORDS_COLLECTION = "words";

    private final MongoDatabase mongoDatabase;

    public WordDAO(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public CloudTag getTargetCloudTag(String twitterId, int limit) {

        MongoCollection<Document> collection = mongoDatabase.getCollection(WORDS_COLLECTION);
        MongoCursor<Document> documentsIterator = collection.find(Filters.eq("target", twitterId))
                .sort(Sorts.descending("count")).limit(limit).iterator();

        List<WordCount> wordCountList = new ArrayList<WordCount>();

        while (documentsIterator.hasNext()) {

            Document document = documentsIterator.next();
            String word = document.getString("word");
            int count = document.getLong("count").intValue();
            WordCount wordCount = new WordCount(word, count);
            wordCountList.add(wordCount);
        }

        return new CloudTag(twitterId, wordCountList);
    }
}
