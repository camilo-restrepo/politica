package followersCrawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TargetDAO {
	
	public static final String IDS_COLLECTION = "twitterIds";

	private final MongoDatabase mongoDatabase;
	private final ObjectMapper objectMapper;

	public TargetDAO(MongoDatabase mongoDatabase, ObjectMapper objectMapper) {
		this.mongoDatabase = mongoDatabase;
		this.objectMapper = objectMapper;
	}

	public List<TwitterId> getAllIds(){
		MongoCollection<Document> collection = mongoDatabase.getCollection(IDS_COLLECTION);
		MongoCursor<Document> it = collection.find().iterator();
		List<TwitterId> ids = new ArrayList<>();
		while(it.hasNext()){
			try {
				TwitterId id = objectMapper.readValue(it.next().toJson(), TwitterId.class);
				ids.add(id);
			} catch (IOException e) {
				//TODO
				e.printStackTrace();
			}
		}
		return ids;
	}
	
	public List<Document> getAllTargetsIds(){
		MongoCollection<Document> targets = mongoDatabase.getCollection(IDS_COLLECTION);
		List<Document> targetsList = new ArrayList<>();
		MongoCursor<Document> it = targets.find().iterator();
		while(it.hasNext()){
			targetsList.add(it.next());
		}
		return targetsList;
	}
}
