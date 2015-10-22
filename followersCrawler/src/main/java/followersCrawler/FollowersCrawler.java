package followersCrawler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.List;

/**
 * Created by Pisco on 10/14/15.
 */
public class FollowersCrawler {

    public final static String CONSUMER_KEY = "UxE2TDERskUyrvFNh8dgMff3F";
    public final static String CONSUMER_SECRET = "OHa7KTY19CPe5wxTymouZC0faSUvyZOb26tcJcCMXiCFZBblrx";
    public final static String ACCESS_TOKEN = "263623229-WLVVIMEY6IPXWpi730WgrFXuvf8LCsiLkohkMNHt";
    public final static String ACCESS_TOKEN_SECRET = "ny4XKoelOHfmNGrRQwKDd84Z7hBY1kBYk7Ia4aGNoRpSD";

    public final static String MONGO_IP = "";
    public final static String MONGO_DB = "boarddb";

    public static void main(String[] args) throws InterruptedException {

        MongoClient mongoClient = new MongoClient(MONGO_IP);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGO_DB);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TargetDAO targetsDAO = new TargetDAO(mongoDatabase, objectMapper);
        FollowersDAO followersDAO = new FollowersDAO(mongoDatabase);

        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        AccessToken token = new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
        twitter.setOAuthAccessToken(token);
        List<TwitterId> targets = targetsDAO.getAllIds();

        while (true) {
            for (TwitterId targetId : targets) {
                try {
                    String stringId = targetId.getId();
                    long cursor = -1;
                    IDs ids = twitter.getFollowersIDs(stringId, cursor);
                    do {
                        for (long id : ids.getIDs()) {
                            followersDAO.insertFollower(id, stringId);
                        }
                        ids = twitter.getFollowersIDs(cursor);
                    } while ((cursor = ids.getNextCursor()) != 0);
                } catch (TwitterException te) {
                    te.printStackTrace();
                    System.out.println("Failed to get followers' ids: " + te.getMessage());
                }
            }
            Thread.sleep(3600000L);
        }
    }
}
