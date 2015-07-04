package crawler.facebook;

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

import crawler.entities.FacebookComment;
import crawler.entities.FacebookPage;
import crawler.entities.FacebookPost;
import crawler.entities.FacebookTarget;

public class FacebookMongoDAO implements FacebookDAO {
	
	public static final String TARGETS_COLLECTION = "facebookTargets";
	public static final String PAGES_COLLECTION = "facebookPages";
	public static final String POSTS_COLLECTION = "facebookPosts";
	public static final String COMMENTS_COLLECTION = "facebookComments";
	
	private final MongoDatabase database;
	private final ObjectMapper objectMapper;
	
	public FacebookMongoDAO(MongoDatabase database, ObjectMapper objectMapper) {
		this.database = database;
		this.objectMapper = objectMapper;
	}
	
	public List<FacebookTarget> getTargets() {
		List<FacebookTarget> targets = new ArrayList<FacebookTarget>();
		MongoCursor<Document> it = database.getCollection(TARGETS_COLLECTION).find().iterator();
		while(it.hasNext()){
			try {
				FacebookTarget target = objectMapper.readValue(it.next().toJson(), FacebookTarget.class);
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

	public void insertPage(FacebookPage page) {
		MongoCollection<Document> collection = database.getCollection(PAGES_COLLECTION);
		try {
			String pageJson = objectMapper.writeValueAsString(page);
			Document pageDocument = Document.parse(pageJson);
			collection.insertOne(pageDocument);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertPost(FacebookPost fbPost) {
		MongoCollection<Document> collection = database.getCollection(POSTS_COLLECTION);
		try {
			String postJson = objectMapper.writeValueAsString(fbPost);
			Document postDocument = Document.parse(postJson);
			collection.insertOne(postDocument);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertComment(FacebookComment fbComment) {
		MongoCollection<Document> collection = database.getCollection(COMMENTS_COLLECTION);
		try {
			String commentJson = objectMapper.writeValueAsString(fbComment);
			Document commentDocument = Document.parse(commentJson);
			collection.insertOne(commentDocument);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}