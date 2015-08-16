package webClassifier.configuration;

import io.dropwizard.Configuration;

public class MongoConfiguration extends Configuration {

	private String dbName;
	private String ip;

	public String getDbName() {
		return dbName;
	}

	public String getIp() {
		return ip;
	}
}
