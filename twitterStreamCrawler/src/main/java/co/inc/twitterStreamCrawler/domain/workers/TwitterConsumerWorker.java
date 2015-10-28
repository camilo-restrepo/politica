package co.inc.twitterStreamCrawler.domain.workers;

import co.inc.twitterStreamCrawler.domain.entities.Prediction;
import co.inc.twitterStreamCrawler.persistence.daos.TargetDAO;
import co.inc.twitterStreamCrawler.persistence.daos.TweetDAO;
import co.inc.twitterStreamCrawler.utils.PolarityClassifier;
import co.inc.twitterStreamCrawler.utils.Twokenize;
import co.inc.twitterStreamCrawler.utils.constants.GlobalConstants;
import co.inc.twitterStreamCrawler.utils.stopwords.classification.StopwordsSpanish;
import com.sun.jersey.api.client.WebResource;
import org.bson.Document;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TwitterConsumerWorker implements Runnable {

	private static final Pattern UNDESIRABLES = Pattern
			.compile("[\\d+\\]\\[\\+(){},.;¡!¿“”?/\\-<>%\r\n\\|\\.,;:\u2026►\"]");
	private static final Pattern SPACE = Pattern.compile(" +");

	private final String stringTweet;
	private final TweetDAO tweetDAO;
	private final TargetDAO targetsDAO;
	private final PolarityClassifier polarityClassifier;
	private final StopwordsSpanish stopwords;

	private FilteredClassifier classifier;

	private final List<Document> targetsList;

	private final WebResource webResource;

	public TwitterConsumerWorker(TargetDAO targetsDAO, TweetDAO tweetDAO, String stringTweet,
								 PolarityClassifier polarityClassifier, StopwordsSpanish stopwords,
								 FilteredClassifier classifier, WebResource webResource) {
		this.tweetDAO = tweetDAO;
		this.stringTweet = stringTweet;
		this.targetsDAO = targetsDAO;
		this.polarityClassifier = polarityClassifier;
		this.stopwords = stopwords;
		this.classifier = classifier;
		this.webResource = webResource;
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
			e.printStackTrace();
		}
	}

	private void updateWordCount(final String text, final String target) {
		String cleanText = GlobalConstants.UNDESIRABLES.matcher(text).replaceAll("");
		//String[] tokens = GlobalConstants.SPACE.split(cleanText);
		List<String> tweetTokens = Twokenize.tokenizeRawTweetText(cleanText);
		for (String token : tweetTokens) {
			if (!token.toLowerCase().equals("rt") && !token.toLowerCase().startsWith("htt")
					&& !token.toLowerCase().startsWith("@") && !stopwords.isStopword(token)) {
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
		Prediction prediction = getPrediction(tweet);
		if (prediction != null) {
			newTweet.append("prediction", prediction.getPrediction());
			newTweet.append("distribution", prediction.getDitribution());
		}
		return newTweet;
	}

	private Prediction getPrediction(Document tweet) {
		String text = tweet.getString("text");
		text = cleanText(text);

		FastVector fvNominalVal = new FastVector(3);
		fvNominalVal.addElement("negative");
		fvNominalVal.addElement("neutral");
		fvNominalVal.addElement("positive");

		Attribute classAttribute = new Attribute("class", fvNominalVal);
		Attribute textAttribute = new Attribute("text", (FastVector) null);

		FastVector fvWekaAttributes = new FastVector(2);
		fvWekaAttributes.addElement(classAttribute);
		fvWekaAttributes.addElement(textAttribute);

		Instances instances = new Instances("Classification", fvWekaAttributes, 1);
		instances.setClassIndex(0);
		Instance instance = new Instance(2);
		instance.setValue(textAttribute, text);
		instances.add(instance);
		try {
			double[] distribution = classifier.distributionForInstance(instances.firstInstance());
			double pred = classifier.classifyInstance(instances.firstInstance());
			String predicted = instances.firstInstance().classAttribute().value((int) pred);

			List<Double> dist = new ArrayList<>();
			for (double d : distribution) {
				dist.add(d);
			}
			return new Prediction(predicted, dist);
		} catch (Exception e) {
			return null;
		}
	}

	private String cleanText(String text) {
		try {
			String newText = UNDESIRABLES.matcher(text).replaceAll("");
			String[] tweetTokens = SPACE.split(newText);
			StringBuilder cleanTweet = new StringBuilder();
			List<String> tokens = new ArrayList<>();
			List<String> tokensSinStopwords = new ArrayList<>();

			for (String token : tweetTokens) {
				token = token.toLowerCase();
				if (!token.equals("rt") && !token.equals("ht")) {
					if (token.startsWith("@")) {
						token = "@twitterusername";
					}
					if (token.startsWith("#")) {
						token = "#twitterhashtag";
					}
					if (token.startsWith("http") || token.startsWith("htt")) {
						token = "twitterurl";
					}
					cleanTweet.append(token).append(" ");
					tokens.add(token);
					if (!stopwords.isStopword(token)) {
						tokensSinStopwords.add(token);
					}
				}
			}
			return cleanTweet.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<String> getTargets(final String text, final List<Document> targetsList) {
		List<String> targets = new ArrayList<>();
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
		sendTweet(documentTweet);
	}

	private void sendTweet(final Document documentTweet) {
		webResource.type("application/json").post(documentTweet.toJson());
	}
}