package crawler.crawler;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import crawler.configuration.CrawlerConfiguration;
import crawler.twitter.TwitterCrawler;
import crawler.twitter.TwitterDAO;
import crawler.twitter.TwitterDynamoDAO;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class Crawler extends Application<CrawlerConfiguration> {

	@Override
	public void initialize(Bootstrap<CrawlerConfiguration> bootstrap) {
	}
	
	private ObjectMapper configureJacksonObjectMapper(Environment environment) {
		ObjectMapper objectMapper = environment.getObjectMapper();
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return objectMapper;
	}
	
	private AmazonDynamoDB getAmazonDynamoDBClient() {

		String accessKey = "AKIAJM4UEST46UVFKTBQ";
		String secretKey = "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";
		AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		AmazonDynamoDB dynamoDBClient = new AmazonDynamoDBClient(awsCredentials);
		dynamoDBClient.setRegion(Region.getRegion(Regions.US_WEST_2));

		return dynamoDBClient;
	}

	@Override
	public void run(CrawlerConfiguration configuration, Environment environment) throws Exception {
		
		ObjectMapper objectMapper = configureJacksonObjectMapper(environment);

//		MongoClient mongoClient = new MongoClient(configuration.getMongoConfiguration().getHost());
//		MongoDatabase database = mongoClient.getDatabase(configuration.getMongoConfiguration().getDatabase());
//		TwitterDAO twitterDAO = new TwitterMongoDAO(database, objectMapper);
		
		AmazonDynamoDB dynamoDBClient = getAmazonDynamoDBClient();
		TwitterDAO twitterDAO = new TwitterDynamoDAO(objectMapper, dynamoDBClient);
		TwitterCrawler crawler = new TwitterCrawler(twitterDAO);
		crawler.init();
		
		environment.jersey().disable();
	}

	public static void main(String[] args) throws Exception {
		Crawler crawler = new Crawler();
		crawler.run(args);
	}
}
