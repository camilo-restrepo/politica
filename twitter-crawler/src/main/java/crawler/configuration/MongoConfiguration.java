package crawler.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MongoConfiguration {
	
	private final String host;
	private final String database;

	@JsonCreator
	public MongoConfiguration(@JsonProperty("host") String host, @JsonProperty("database") String database) {
		this.host = host;
		this.database = database;
	}

	public String getHost() {
		return host;
	}

	public String getDatabase() {
		return database;
	}
}
