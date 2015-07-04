package crawler.twitter;

import java.util.List;

import crawler.entities.Tweet;
import crawler.entities.TwitterTarget;
import crawler.entities.TwitterUser;

public interface TwitterDAO {
	
	public static final String TARGETS_COLLECTION = "twitterTargets";
	public static final String USERS_COLLECTION = "twitterUsers";
	public static final String TWEETS_COLLECTION = "tweets";
	
	public List<TwitterTarget> getTargets();
	
	public void insertUser(TwitterUser twitterUser);
	
	public void insertTweet(Tweet tweet);
	
	public List<Tweet> getAllTweets(int offset, int limit);
	
	public List<Tweet> getAllTweets(String lastEvaluatedTwitterId, int limit);
}