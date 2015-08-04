package co.inc.twitterStreamCrawler.utils;

import org.bson.Document;

import co.inc.twitterStreamCrawler.persistence.daos.TweetDAO;

public class PolarityWorker implements Runnable {
	
	private final PolarityClassifier classifier;
	private final TweetDAO tweetDAO;
	private final Document doc;
	private final int i;
	
	public PolarityWorker(PolarityClassifier classifier, TweetDAO tweetDAO, Document doc, int  i){
		this.classifier = classifier;
		this.tweetDAO = tweetDAO;
		this.doc = doc;
		this.i = i;
	}
	
	@Override
	public void run() {
		Document updatedDocument = new Document(doc);
		if (!doc.containsKey("polarity")) {
			String tweetText = (String) doc.get("text");
			int polarity = classifier.getTweetPolarity(tweetText);
			updatedDocument.append("polarity", polarity);
			tweetDAO.updateTweetPolarity(doc, updatedDocument);			
		}
		System.out.println(i);
	}
}