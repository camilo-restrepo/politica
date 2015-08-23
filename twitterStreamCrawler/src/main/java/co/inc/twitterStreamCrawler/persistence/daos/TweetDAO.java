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
	public static final String WORDS_COLLECTION = "words";

	private final MongoDatabase mongoDatabase;

	public TweetDAO(MongoDatabase mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
	}

	public synchronized void insertTweet(final Document documentTweet) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		collection.insertOne(documentTweet);
	}
	
	public synchronized void insertCleanTweet(final Document minTweet) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(MINIMUM_TWEETS);
		collection.insertOne(minTweet);
	}
	
	public synchronized List<Document> getAllTweets(){
		List<Document> result = new ArrayList<>();
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		MongoCursor<Document> it = collection.find().iterator();
		while(it.hasNext()){
			result.add(it.next());
		}
		it.close();
		return result;
	}
	
	public synchronized void updateTweetPolarity(final Document oldTweet, final Document newTweet){
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		collection.replaceOne(oldTweet, newTweet);
	}
	
	public synchronized void incrementWordCount(final String word, final String candidate){
		MongoCollection<Document> collection = mongoDatabase.getCollection(WORDS_COLLECTION);
		Document modifier = new Document("count", 1);
		Document incQuery = new Document("$inc", modifier);
		Document searchQuery = new Document("word", word);
		searchQuery.put("target", candidate);
		collection.updateOne(searchQuery, incQuery);
	}
}