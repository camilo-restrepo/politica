package crawler.crawler;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import crawler.configuration.CrawlerConfiguration;
import crawler.facebook.FacebookCrawler;

public class Crawler extends Application<CrawlerConfiguration> {

	@Override
	public void initialize(Bootstrap<CrawlerConfiguration> bootstrap) {
	}

	@Override
	public void run(CrawlerConfiguration configuration, Environment environment) throws Exception {
		ObjectMapper objectMapper = configureJacksonObjectMapper(environment);

		MongoClient mongoClient = new MongoClient(configuration.getMongoConfiguration().getHost());
		MongoDatabase database = mongoClient.getDatabase(configuration.getMongoConfiguration().getDatabase());

		FacebookCrawler fbCrawler = new FacebookCrawler(database, objectMapper);
		fbCrawler.init();
		
		environment.jersey().disable();
	}

	private ObjectMapper configureJacksonObjectMapper(Environment environment) {
		ObjectMapper objectMapper = environment.getObjectMapper();
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return objectMapper;
	}
	
	public static void main(String[] args) throws Exception {
		Crawler crawler = new Crawler();
		crawler.run(args);
	}
}
