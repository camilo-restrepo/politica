package co.inc.twitterStreamCrawler.persistence.daos;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class TweetDAO {

	public static final String TWEETS_COLLECTION = "tweets";
	public static final String MINIMUM_TWEETS = "minimumTweets";

	private final MongoDatabase mongoDatabase;

	public TweetDAO(MongoDatabase mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
	}

	public void insertTweet(Document documentTweet) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		collection.insertOne(documentTweet);
	}
	
	public void insertCleanTweet(Document minTweet) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(MINIMUM_TWEETS);
		collection.insertOne(minTweet);
	}
	
	public List<Document> getAllTweets(){
		List<Document> result = new ArrayList<>();
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		MongoCursor<Document> it = collection.find().iterator();
		while(it.hasNext()){
			result.add(it.next());
		}
		it.close();
		return result;
	}
	
	public void updateTweetPolarity(Document oldTweet, Document newTweet){
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		collection.replaceOne(oldTweet, newTweet);
	}
}