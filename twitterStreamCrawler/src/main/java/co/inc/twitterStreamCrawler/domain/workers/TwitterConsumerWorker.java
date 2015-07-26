package co.inc.twitterStreamCrawler.domain.workers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.inc.twitterStreamCrawler.domain.entities.TwitterId;
import co.inc.twitterStreamCrawler.persistence.daos.TargetsDAO;
import co.inc.twitterStreamCrawler.persistence.daos.TweetDAO;

import com.google.common.collect.Lists;

public class TwitterConsumerWorker implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterConsumerWorker.class);

	private final String stringTweet;
	private final TweetDAO tweetDAO;
	private final TargetsDAO targetsDAO;

	public TwitterConsumerWorker(TargetsDAO targetsDAO, TweetDAO tweetDAO, String stringTweet) {
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
		//TODO Enviar Tweet
	}
}