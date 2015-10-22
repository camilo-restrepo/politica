package followersCrawler;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by Pisco on 10/14/15.
 */
public class FollowersDAO {

    public static final String FOLLOWERS_COLLECTION = "followersIds";

    private final MongoDatabase mongoDatabase;

    public FollowersDAO(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public synchronized void insertFollower(final long id, final String targetId){
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection(FOLLOWERS_COLLECTION);
            Document document = new Document();
            document.put("followerId", id);
            document.put("targetid", targetId);
            document.put("timestamp", System.currentTimeMillis());
            collection.insertOne(document);
        }catch(Exception e){

        }
    }
}
