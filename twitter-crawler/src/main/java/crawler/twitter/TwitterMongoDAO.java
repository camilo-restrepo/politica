package crawler.twitter;

import static com.mongodb.client.model.Projections.excludeId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import crawler.entities.Tweet;
import crawler.entities.TwitterTarget;
import crawler.entities.TwitterUser;

public class TwitterMongoDAO implements TwitterDAO {

	private final MongoDatabase database;
	private final ObjectMapper objectMapper;

	public TwitterMongoDAO(MongoDatabase database, ObjectMapper objectMapper) {
		this.database = database;
		this.objectMapper = objectMapper;
	}

	public List<TwitterTarget> getTargets() {
		List<TwitterTarget> targets = new ArrayList<TwitterTarget>();
		MongoCursor<Document> it = database.getCollection(TARGETS_COLLECTION).find().iterator();
		while (it.hasNext()) {
			try {
				TwitterTarget target = objectMapper.readValue(it.next().toJson(), TwitterTarget.class);
				targets.add(target);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		it.close();
		return targets;
	}

	public void insertUser(TwitterUser twitterUser) {
		MongoCollection<Document> collection = database.getCollection(USERS_COLLECTION);
		try {
			String postJson = objectMapper.writeValueAsString(twitterUser);
			Document postDocument = Document.parse(postJson);
			collection.insertOne(postDocument);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertTweet(Tweet tweet) {
		MongoCollection<Document> collection = database.getCollection(TWEETS_COLLECTION);
		try {
			String postJson = objectMapper.writeValueAsString(tweet);
			Document postDocument = Document.parse(postJson);
			collection.insertOne(postDocument);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Tweet> getAllTweets(int offset, int limit) {
		List<Tweet> tweets = new ArrayList<Tweet>();
		MongoCollection<Document> collection = database.getCollection(TWEETS_COLLECTION);
		MongoCursor<Document> it = collection.find().projection(excludeId()).skip(offset).limit(limit).iterator();
		while (it.hasNext()) {
			try {
				String j = it.next().toJson();
				System.out.println(j);
				Tweet tweet = objectMapper.readValue(j, Tweet.class);
				tweets.add(tweet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		it.close();
		return tweets;
	}
	
	public List<Tweet> getAllTweets(String lastEvaluatedTwitterId, int limit) {
		
		String message = "MongoDB does not support this type of pagination.";
		throw new IllegalStateException(message);
	}
}
