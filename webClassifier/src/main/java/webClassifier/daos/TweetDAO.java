package webClassifier.daos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import webClassifier.entities.ClassifiedTweet;
import webClassifier.entities.Tweet;

public class TweetDAO {

	public static final String TWEETS_COLLECTION = "onlyText";
	public static final String CLASSIFIED_TWEETS_COLLECTION = "classifiedTweets";
	public static final String ORIGINAL_TWEETS = "tweets";

	private final Datastore ds;
	private final MongoDatabase database;

	public TweetDAO(Datastore ds, MongoDatabase database) {
		this.ds = ds;
		this.database = database;
	}

	public List<Tweet> getTweets(int offset, int limit) {
		Random r = new Random();
		offset = r.nextInt((int) database.getCollection(ORIGINAL_TWEETS).count());
		List<Tweet> tweets = ds.find(Tweet.class).limit(limit).offset(offset).asList();
		List<Tweet> result = new ArrayList<>(); 
		for(Tweet t : tweets){
			long id = Long.parseLong(t.getId());
			MongoCollection<Document> originaltweets = database.getCollection(ORIGINAL_TWEETS);
			BasicDBObject filter = new BasicDBObject();
			filter.put("id", id);
			MongoCursor<Document> it = originaltweets.find(filter).iterator();
			Document doc = it.next();
			int polarity = (int) doc.get("polarity");
			t.setClassification(polarity);
			result.add(t);
		}
		return result;
	}

	public void updateTweet(ClassifiedTweet classifiedTweet){
		ds.save(classifiedTweet);
		Query<Tweet> deleteQuery = ds.createQuery(Tweet.class).filter("id =", classifiedTweet.getId());
		ds.delete(deleteQuery);
	}

	public void deleteTweet(String id) {
		Query<Tweet> deleteQuery = ds.createQuery(Tweet.class).filter("id =", Long.parseLong(id));
		ds.delete(deleteQuery);
	}
}
