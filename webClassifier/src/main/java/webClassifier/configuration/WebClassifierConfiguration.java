package webClassifier.configuration;

import io.dropwizard.Configuration;

public class WebClassifierConfiguration extends Configuration{
	
	private MongoConfiguration mongoConfiguration;

	public MongoConfiguration getMongoConfiguration() {
		return mongoConfiguration;
	}
}
