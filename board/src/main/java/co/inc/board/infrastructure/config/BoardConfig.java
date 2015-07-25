package co.inc.board.infrastructure.config;

import io.dropwizard.Configuration;

public class BoardConfig extends Configuration {
	
	private MongoConfig mongoConfig;

	public MongoConfig getMongoConfig() {
		return mongoConfig;
	}
}