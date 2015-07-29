package co.inc.twitterStreamCrawler.domain.workers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.inc.twitterStreamCrawler.domain.dto.TweetDTO;
import co.inc.twitterStreamCrawler.domain.entities.TwitterId;
import co.inc.twitterStreamCrawler.persistence.daos.TargetDAO;
import co.inc.twitterStreamCrawler.persistence.daos.TweetDAO;
import co.inc.twitterStreamCrawler.utils.PolarityClassifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class TwitterConsumerWorker implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterConsumerWorker.class);

	private final String stringTweet;
	private final TweetDAO tweetDAO;
	private final TargetDAO targetsDAO;
	private final PolarityClassifier polarityClassifier;

	public TwitterConsumerWorker(TargetDAO targetsDAO, TweetDAO tweetDAO, String stringTweet,
			PolarityClassifier polarityClassifier) {
		this.tweetDAO = tweetDAO;
		this.stringTweet = stringTweet;
		this.targetsDAO = targetsDAO;
		this.polarityClassifier = polarityClassifier;
	}

	@Override
	public void run() {
		Document documentTweet = getTweetWithTargets();
		documentTweet = getTweetWithPolarity(documentTweet);
		tweetDAO.insertTweet(documentTweet);
		sendTweetToBoard(documentTweet);
	}

	private Document getTweetWithPolarity(Document documentTweet) {
		String tweetText = (String) documentTweet.get("text");
		int polarity = polarityClassifier.getTweetPolarity(tweetText);
		documentTweet.append("polarity", polarity);
		return documentTweet;
	}

	private Document getTweetWithTargets() {
		Document documentTweet = Document.parse(stringTweet);
		String tweetText = (String) documentTweet.get("text");
		List<String> foundtargets = new ArrayList<String>();
		List<TwitterId> ids = targetsDAO.getAllIds();
		for (TwitterId target : ids) {
			List<String> relatedWords = target.getRelatedWords();
			for (String word : relatedWords) {
				if (tweetText.toLowerCase().contains(word.toLowerCase())) {
					foundtargets.add(target.getId());
					break;
				}
			}
		}
		documentTweet.append("targetTwitterIds", foundtargets);
		return documentTweet;
	}

	private TweetDTO getTweetDTO(Document documentTweet) {
		String text = documentTweet.getString("text");
		List<String> targets = (List<String>) documentTweet.get("targetTwitterIds");
		long favorites = documentTweet.getInteger("favorite_count").longValue();
		long retweets = documentTweet.getInteger("retweet_count").longValue();
		long date = Long.parseLong(documentTweet.getString("timestamp_ms"));
		String userId = ((Document) documentTweet.get("user")).getString("screen_name");
		String screenName = ((Document) documentTweet.get("user")).getString("name");
		String userImageUrl = ((Document) documentTweet.get("user")).getString("profile_image_url");
		int polarity = documentTweet.getInteger("polarity");
		TweetDTO tweetDTO = new TweetDTO(text, targets, retweets, favorites, date, screenName, userId, userImageUrl, polarity);
		return tweetDTO;
	}

	private void sendTweetToBoard(Document documentTweet) {
		TweetDTO tweetDTO = getTweetDTO(documentTweet);
		try {
			sendTweet(tweetDTO);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendTweet(TweetDTO tweetDTO) throws IOException {
		Client client = Client.create();
		WebResource webResource = client.resource("http://localhost:9001/board/api/broadcast");
		String input = getJsonString(tweetDTO);
		// System.out.println("Sending... " + input);
		webResource.type("application/json").post(input);
		client.destroy();
	}

	private String getJsonString(TweetDTO tweetDTO) throws JsonProcessingException {
		ObjectMapper objectMapper = getObjectMapper();
		String jsonTweet = objectMapper.writeValueAsString(tweetDTO);
		return jsonTweet;
	}

	private ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}
}