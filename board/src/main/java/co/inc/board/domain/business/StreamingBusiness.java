package co.inc.board.domain.business;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class StreamingBusiness {

	private final static String CONSUMER_KEY = "wxzRapS5chgLj2mk4I4A";
	private final static String CONSUMER_SECRET = "FVc5dgZ05j288pf0mKUQuvqeJsP550nnVvxUqINdI";
	private final static String ACCESS_TOKEN = "263623229-NEcwbcSBdDnYxtnoFFdbi4VOPCtdTjBpwnSc5a8b";
	private final static String ACCESS_TOKEN_SECRET = "eWXxlbezHqQN4NFyPZe5TGxwinhOVEeERDhU9irT5cXTc";

	private final BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
	private final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

	public Client getHosebirdClient(List<String> terms) {

		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		hosebirdEndpoint.trackTerms(terms);
		Authentication hosebirdAuth = new OAuth1(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01").hosts(hosebirdHosts).authentication(hosebirdAuth)
				.endpoint(hosebirdEndpoint).processor(new StringDelimitedProcessor(msgQueue)).eventMessageQueue(eventQueue);
		Client hosebirdClient = builder.build();
		return hosebirdClient;
	}

	public BlockingQueue<String> getMsgQueue() {
		return msgQueue;
	}

	public BlockingQueue<Event> getEventQueue() {
		return eventQueue;
	}
}