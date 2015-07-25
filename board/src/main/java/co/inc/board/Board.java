package co.inc.board;

import io.dropwizard.Application;
import io.dropwizard.java8.Java8Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.inc.board.infrastructure.config.BoardConfig;
import co.inc.board.infrastructure.config.MongoConfig;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
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

public class Board extends Application<BoardConfig> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Board.class);

	private final static String CONSUMER_KEY = "wxzRapS5chgLj2mk4I4A";
	private final static String CONSUMER_SECRET = "FVc5dgZ05j288pf0mKUQuvqeJsP550nnVvxUqINdI";
	private final static String ACCESS_TOKEN = "263623229-NEcwbcSBdDnYxtnoFFdbi4VOPCtdTjBpwnSc5a8b";
	private final static String ACCESS_TOKEN_SECRET = "eWXxlbezHqQN4NFyPZe5TGxwinhOVEeERDhU9irT5cXTc";

	@Override
	public void initialize(Bootstrap<BoardConfig> bootstrap) {

		bootstrap.addBundle(new Java8Bundle());
	}

	private void addCORSSupport(Environment environment) {

		Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS,PATCH");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		filter.setInitParameter("allowCredentials", "true");
	}

	private ObjectMapper configureJackson(Environment environment) {

		ObjectMapper objectMapper = environment.getObjectMapper();
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return objectMapper;
	}

	private MongoClient getMongoClient(BoardConfig boardConfig) {
		MongoConfig mongoConfig = boardConfig.getMongoConfig();
		MongoClient mongoClient = new MongoClient(mongoConfig.getDbName());

		return mongoClient;
	}

	@Override
	public void run(BoardConfig boardConfig, Environment environment) throws Exception {

		// add CORS support.
		addCORSSupport(environment);

		// Configure Jackson serialization and deserialization.
		ObjectMapper objectMapper = configureJackson(environment);

		MongoClient mongoClient = getMongoClient(boardConfig);
	}

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {

		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
		BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

		/**
		 * Declare the host you want to connect to, the endpoint, and
		 * authentication (basic auth or oauth)
		 */
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		// Optional: set up some followings and track terms
		List<Long> followings = Lists.newArrayList(401001421L, 263623229L);
		List<String> terms = Lists.newArrayList("Carlos Vicente de Roux", "CVderoux");
		hosebirdEndpoint.followings(followings);
//		hosebirdEndpoint.trackTerms(terms);

		// These secrets should be read from a config file
		Authentication hosebirdAuth = new OAuth1(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET);

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01")
				// optional: mainly for the logs
				.hosts(hosebirdHosts).authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue)).eventMessageQueue(eventQueue);

		Client hosebirdClient = builder.build();
		// Attempts to establish a connection.
		hosebirdClient.connect();

		long count = 0;
//		System.setOut(new PrintStream(new File("./out2.txt")));
		while (!hosebirdClient.isDone()) {
			String msg = msgQueue.take();
			count++;
			System.out.println(count+": "+msg+"\r\n");
		}
		// try {
		//
		// Board board = new Board();
		// board.run(args);
		//
		// } catch (Exception e) {
		//
		// LOGGER.error("main", e);
		// }
	}
}
