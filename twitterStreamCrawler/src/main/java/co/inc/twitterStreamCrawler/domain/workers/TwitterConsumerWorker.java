package co.inc.twitterStreamCrawler.domain.workers;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.inc.twitterStreamCrawler.domain.dto.TweetDTO;
import co.inc.twitterStreamCrawler.domain.entities.TwitterId;
import co.inc.twitterStreamCrawler.persistence.daos.TargetDAO;
import co.inc.twitterStreamCrawler.persistence.daos.TweetDAO;

public class TwitterConsumerWorker implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterConsumerWorker.class);

	private final String stringTweet;
	private final TweetDAO tweetDAO;
	private final TargetDAO targetsDAO;

	public TwitterConsumerWorker(TargetDAO targetsDAO, TweetDAO tweetDAO, String stringTweet) {
		this.tweetDAO = tweetDAO;
		this.stringTweet = stringTweet;
		this.targetsDAO = targetsDAO;
	}

	@Override
	public void run() {
		Document documentTweet = Document.parse(stringTweet);
		String tweetText = (String) documentTweet.get("text");
		List<String> foundtargets = new ArrayList<String>();
		List<TwitterId> ids = targetsDAO.getAllIds();
		for(TwitterId target : ids){
			List<String> relatedWords = target.getRelatedWords();
			for(String word : relatedWords){
				if(tweetText.toLowerCase().contains(word.toLowerCase())){
					foundtargets.add(target.getId());
					break;
				}
			}
		}
		
		documentTweet.append("targetTwitterIds", foundtargets);
		tweetDAO.insertTweet(documentTweet);
		//TODO
		String text = documentTweet.getString("text");
		List<String> targets = (List<String>) documentTweet.get("targetTwitterIds");
		long favorites = documentTweet.getLong("favorite_count");
		long retweets = documentTweet.getLong("retweet_count");
		long date = documentTweet.getLong("timestamp_ms");
		String userId = ((Document) documentTweet.get("user")).getString("screen_name");
		String screenName = ((Document) documentTweet.get("user")).getString("name");
		String userImageUrl = ((Document) documentTweet.get("user")).getString("profile_image_url");
		TweetDTO tweetDTO = new TweetDTO(text, targets, retweets, favorites, date, screenName, userId, userImageUrl);
		//TODO
	}
}