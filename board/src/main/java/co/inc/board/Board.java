package co.inc.board;

import io.dropwizard.Application;
import io.dropwizard.java8.Java8Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.inc.board.domain.business.StreamingBusiness;
import co.inc.board.domain.entities.TwitterTarget;
import co.inc.board.domain.workers.TwitterConsumerWorker;
import co.inc.board.infrastructure.config.BoardConfig;
import co.inc.board.infrastructure.config.MongoConfig;
import co.inc.board.persistence.daos.TweetDAO;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
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

	@Override
	public void initialize(Bootstrap<BoardConfig> bootstrap) {

		bootstrap.addBundle(new Java8Bundle());
	}

	private void addCORSSupport(Environment environment) {

		Dynamic filter = environment.servlets().addFilter("CORS",
				CrossOriginFilter.class);
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),
				true, "/*");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM,
				"GET,PUT,POST,DELETE,OPTIONS,PATCH");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		filter.setInitParameter(
				CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		filter.setInitParameter("allowedHeaders",
				"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		filter.setInitParameter("allowCredentials", "true");
	}

	private ObjectMapper configureJackson(Environment environment) {

		ObjectMapper objectMapper = environment.getObjectMapper();
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(
				SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(
				DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return objectMapper;
	}

	private MongoClient getMongoClient(BoardConfig boardConfig) {
		
		MongoConfig mongoConfig = boardConfig.getMongoConfig();
		return new MongoClient(mongoConfig.getDbName());
	}

	@Override
	public void run(BoardConfig boardConfig, Environment environment)
			throws Exception {

		// add CORS support.
		addCORSSupport(environment);

		// Configure Jackson serialization and deserialization.
		ObjectMapper objectMapper = configureJackson(environment);

		MongoClient mongoClient = getMongoClient(boardConfig);
	}

	public static void main(String[] args) throws InterruptedException,
			FileNotFoundException {

		long twitterId = 62945553;
		String screenName = "cvderoux";
		List<String> relatedWords = Lists.newArrayList("Carlos Vicente De Roux", "Carlos Vicente de Roux", "cvderoux");
		TwitterTarget twitterTarget = new TwitterTarget(twitterId, screenName, relatedWords);
		
		List<TwitterTarget> targets = new ArrayList<TwitterTarget>();
		targets.add(twitterTarget);
		
		//-------------------------------------------------------------------------
		
		int numberOfThreads = targets.size();
		ExecutorService threadPool = Executors.newFixedThreadPool(numberOfThreads);

		MongoClient mongoClient = new MongoClient();
		String databaseName = "boarddb";
		MongoDatabase database = mongoClient.getDatabase(databaseName);
		TweetDAO tweetDAO = new TweetDAO(database);
		
		for (TwitterTarget target : targets) {
			
			StreamingBusiness streamingBusiness = new StreamingBusiness();
			TwitterConsumerWorker worker = new TwitterConsumerWorker(streamingBusiness, target, tweetDAO);
			threadPool.submit(worker);
		}
		
		System.out.println("************************************** 1");

		
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
