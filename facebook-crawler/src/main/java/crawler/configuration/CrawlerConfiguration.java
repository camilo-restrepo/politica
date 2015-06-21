package crawler.configuration;

import io.dropwizard.Configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CrawlerConfiguration extends Configuration {
	
	private final MongoConfiguration mongoConfiguration;

	@JsonCreator
	public CrawlerConfiguration(@JsonProperty("mongoConfiguration") MongoConfiguration mongoConfiguration) {
		
		this.mongoConfiguration = mongoConfiguration;
	}

	public MongoConfiguration getMongoConfiguration() {
		return mongoConfiguration;
	}
}