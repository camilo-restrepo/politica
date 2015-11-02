package co.inc.board.persistence.daos;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pisco on 10/30/15.
 */
public class UsersDAO {

    public static final String USERS_COLLECTION = "users";
    public static final String USER_CREATION_COLLECTION = "userCreation";
    public static final String TARGET_USER_CREATION_COLLECTION = "targetUserCreation";
    public static final String VENN_COLLECTION = "venn";

    private final MongoDatabase mongoDatabase;

    public UsersDAO(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public long getCandidateUsersCount(String targetId){
        MongoCollection<Document> collection = mongoDatabase.getCollection(USERS_COLLECTION);
        Document filter = new Document();
        filter.put("target", targetId);
        long count = collection.count(filter);
        return count;
    }

    public List<Document> getTargetUsers(String twitterId) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(USERS_COLLECTION);
        MongoCursor<Document> it = collection.find().projection(Projections.excludeId()).projection(Projections.exclude("target"))
                .filter(Filters.eq("target", twitterId)).sort(Sorts.descending("count")).limit(10).iterator();
        List<Document> result = new ArrayList<>();
        while(it.hasNext()){
            result.add(it.next());
        }
        it.close();
        return result;
    }

    public List<Document> getUserCreationDateSummary(){
        MongoCollection<Document> collection = mongoDatabase.getCollection(USER_CREATION_COLLECTION);
        MongoCursor<Document> it = collection.find().projection(Projections.excludeId()).sort(Sorts.ascending("time"))
                .filter(Filters.gt("time", 1230699600000L)).iterator();
        List<Document> result = new ArrayList<>();
        while(it.hasNext()){
            result.add(it.next());
        }
        it.close();
        return result;
    }

    public List<Document> getUserCreationCandidate(String twitterId){
        MongoCollection<Document> collection = mongoDatabase.getCollection(TARGET_USER_CREATION_COLLECTION);
        MongoCursor<Document> it = collection.find().projection(Projections.excludeId()).sort(Sorts.ascending("timestamp"))
                .filter(Filters.and(Filters.gt("timestamp", 1230699600000L), Filters.eq("target", twitterId))).iterator();
        List<Document> result = new ArrayList<>();
        while(it.hasNext()){
            result.add(it.next());
        }
        it.close();
        return result;
    }

    public List<Document> getVennData(){
        MongoCollection<Document> collection = mongoDatabase.getCollection(VENN_COLLECTION);
        MongoCursor<Document> it = collection.find().projection(Projections.excludeId()).iterator();
        List<Document> result = new ArrayList<>();
        while(it.hasNext()){
            result.add(it.next());
        }
        it.close();
        return result;
    }
}