package co.inc.board.persistence.daos;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class TweetDAO {

	public static final String TWEETS_COLLECTION = "tweets";

	private final MongoDatabase mongoDatabase;

	public TweetDAO(MongoDatabase mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
	}

	public void insertTweet(long targetTwitterId, String stringTweet) {

		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		Document documentTweet = Document.parse(stringTweet);
		documentTweet.put("targetTwitterId", targetTwitterId);
		collection.insertOne(documentTweet);
	}

	public void insertTweet(String stringTweet) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		Document documentTweet = Document.parse(stringTweet);
		collection.insertOne(documentTweet);
	}
}