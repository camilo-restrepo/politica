package crawler.twitter;

import java.util.List;

import crawler.entities.Tweet;
import crawler.entities.TwitterTarget;
import crawler.entities.TwitterUser;
import twitter4j.GeoLocation;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

//https://github.com/yusuke/twitter4j/blob/master/twitter4j-examples/src/main/java/twitter4j/examples/timeline/GetUserTimeline.java

// Hay limite de 180 peticiones en 15 minutes window...
public class TwitterCrawler {

	private final static String CONSUMER_KEY = "wxzRapS5chgLj2mk4I4A";
	private final static String CONSUMER_SECRET = "FVc5dgZ05j288pf0mKUQuvqeJsP550nnVvxUqINdI";
	private final static String ACCESS_TOKEN = "263623229-NEcwbcSBdDnYxtnoFFdbi4VOPCtdTjBpwnSc5a8b";
	private final static String ACCESS_TOKEN_SECRET = "eWXxlbezHqQN4NFyPZe5TGxwinhOVEeERDhU9irT5cXTc";

	private final TwitterDAO dao;

	public TwitterCrawler(TwitterDAO twitterDAO) {
		dao = twitterDAO;
	}

	public void init() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN).setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		List<TwitterTarget> targets = dao.getTargets();
		for (TwitterTarget target : targets) {
			try {
				String user = target.getId();
				Paging paging = new Paging(1, 200);
				List<Status> statuses = twitter.getUserTimeline(user, paging);
				for (Status status : statuses) {
					TwitterUser twitterUser = new TwitterUser(status.getUser().getId(), status.getUser().getScreenName());
					GeoLocation geoLocation = status.getGeoLocation();
					double latitude = geoLocation == null ? Double.NaN : geoLocation.getLatitude();
					double longitude = geoLocation == null ? Double.NaN : geoLocation.getLongitude();
					Tweet tweet = new Tweet(status.getId(), status.getCreatedAt().getTime(), status.getFavoriteCount(),
							status.getRetweetCount(), status.getText(), target, twitterUser, latitude, longitude);
					dao.insertUser(twitterUser);
					dao.insertTweet(tweet);
				}
				//Pbt: el user y target son el mismo. No toma el usuario que realiza el tweet como tal
				
				
				// Se necesita el usuario del candidato => requiere login y
				// autorizacion de la app
				// System.out.println("------------");
				// List<Status> mentions = twitter.getMentionsTimeline();
				// for (Status mention : mentions) {
				// System.out.println("@" + mention.getUser().getScreenName() +
				// " - " + mention.getText());
				// }
			} catch (TwitterException te) {
				te.printStackTrace();
				System.out.println("Failed to get timeline: " + te.getMessage());
			}
		}
	}
}
