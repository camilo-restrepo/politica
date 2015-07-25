package co.inc.board.domain.workers;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.inc.board.domain.business.StreamingBusiness;
import co.inc.board.domain.entities.TwitterTarget;
import co.inc.board.persistence.daos.TweetDAO;

import com.twitter.hbc.core.Client;

public class TwitterConsumerWorker implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterConsumerWorker.class);
	
	private final StreamingBusiness streamingBusiness;
	private final TwitterTarget twitterTarget;
	private final TweetDAO tweetDAO;

	public TwitterConsumerWorker(StreamingBusiness streamingBusiness, TwitterTarget twitterTarget, 
			TweetDAO tweetDAO) {
		this.streamingBusiness = streamingBusiness;
		this.twitterTarget = twitterTarget;
		this.tweetDAO = tweetDAO;
	}

	@Override
	public void run() {
		Client hosebirdClient = streamingBusiness.getHosebirdClient(twitterTarget);
		hosebirdClient.connect();
		BlockingQueue<String> msgQueue = streamingBusiness.getMsgQueue();
		while (!hosebirdClient.isDone()) {
			try {
				String stringTweet = msgQueue.take();
				tweetDAO.insertTweet(twitterTarget.getTwitterId(), stringTweet);
			} catch (InterruptedException e) {
				LOGGER.error("run", e);
			}
		}
	}
}