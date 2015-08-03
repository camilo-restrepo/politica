package co.inc.twitterStreamCrawler.persistence.daos;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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
		Document documentTweet = Document.parse(stringTweet);
		insertTweet(documentTweet);
	}
	
	public void insertTweet(Document documentTweet) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		collection.insertOne(documentTweet);
	}
	
	public List<Document> getAllTweets(){
		List<Document> result = new ArrayList<>();
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		MongoCursor<Document> it = collection.find().iterator();
		while(it.hasNext()){
			result.add(it.next());
		}
		return result;
	}
	
	public void updateTweetPolarity(Document oldTweet, Document newTweet){
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		collection.replaceOne(oldTweet, newTweet);
	}
}