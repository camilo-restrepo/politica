package co.inc.twitterStreamCrawler.domain.workers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import co.inc.twitterStreamCrawler.persistence.daos.TargetDAO;
import co.inc.twitterStreamCrawler.persistence.daos.TweetDAO;
import co.inc.twitterStreamCrawler.utils.PolarityClassifier;
import co.inc.twitterStreamCrawler.utils.constants.GlobalConstants;
import co.inc.twitterStreamCrawler.utils.stopwords.classification.StopwordsSpanish;

public class TwitterConsumerWorker implements Runnable {

	
	private final String stringTweet;
	private final TweetDAO tweetDAO;
	private final TargetDAO targetsDAO;
	private final PolarityClassifier polarityClassifier;
	private final StopwordsSpanish stopwords;

	private final List<Document> targetsList;

	public TwitterConsumerWorker(TargetDAO targetsDAO, TweetDAO tweetDAO, String stringTweet,
			PolarityClassifier polarityClassifier, StopwordsSpanish stopwords) {
		this.tweetDAO = tweetDAO;
		this.stringTweet = stringTweet;
		this.targetsDAO = targetsDAO;
		this.polarityClassifier = polarityClassifier;
		this.stopwords = stopwords;
		targetsList = this.targetsDAO.getAllTargetsIds();
	}

	@Override
	public void run() {
		// Persist Tweet as is
		Document tweetDocument = getTweetDocumentFormnString();
		tweetDAO.insertTweet(tweetDocument);
		// Remove junk from tweet
		try {
			Document cleanTweet = cleanTweet(tweetDocument);
			List<String> targets = getTargets(cleanTweet.getString("text"), targetsList);
			for (String target : targets) {
				Document minTweet = new Document(cleanTweet);
				minTweet.append("targetTwitterId", target);
				tweetDAO.insertCleanTweet(minTweet);
				updateWordCount(cleanTweet.getString("text"), target);
				sendTweetToBoard(minTweet);
			}
		} catch (Exception e) {
		}
	}

	private void updateWordCount(final String text, final String target) {
		String cleanText = GlobalConstants.UNDESIRABLES.matcher(text).replaceAll("");
		String[] tokens = GlobalConstants.SPACE.split(cleanText);
		for (String token : tokens) {
			if (!token.startsWith("@") && !token.startsWith("#") && !stopwords.isStopword(token)) {
				tweetDAO.incrementWordCount(token, target);
			}
		}
	}

	private Document cleanTweet(final Document tweet) {
		Document newTweet = new Document();
		newTweet.append("id", tweet.get("id"));
		newTweet.append("text", tweet.get("text"));
		newTweet.append("geo", tweet.get("geo"));
		newTweet.append("timestamp_ms", Long.parseLong(tweet.getString("timestamp_ms")));
		int polarity = getTweetPolarity(tweet.getString("text"));
		newTweet.append("polarity", polarity);
		return newTweet;
	}

	private List<String> getTargets(final String text, final List<Document> targetsList) {
		List<String> targets = new ArrayList<String>();
		for (Document target : targetsList) {
			List<String> relatedWords = (List<String>) target.get("relatedWords");
			for (String word : relatedWords) {
				if (text.toLowerCase().contains(word.toLowerCase())) {
					targets.add(target.getString("id"));
					break;
				}
			}
		}
		return targets;
	}

	private Document getTweetDocumentFormnString() {
		Document tweetDocument = Document.parse(stringTweet);
		return tweetDocument;
	}

	private int getTweetPolarity(String text) {
		int polarity = polarityClassifier.getTweetPolarity(text);
		return polarity;
	}

	private void sendTweetToBoard(final Document documentTweet) {
		try {
			sendTweet(documentTweet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendTweet(final Document documentTweet) throws IOException {
		Client client = Client.create();
		WebResource webResource = client.resource(GlobalConstants.BOARD_URL);
		webResource.type("application/json").post(documentTweet.toJson());
		client.destroy();
	}
}