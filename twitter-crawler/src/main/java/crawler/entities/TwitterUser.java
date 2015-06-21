package crawler.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitterUser {
	
	private long twitterId;
	private String name;

	@JsonCreator
	public TwitterUser(@JsonProperty("twitterId") long twitterId, @JsonProperty("name") String name){
		this.twitterId = twitterId;
		this.name = name;
	}

	public long getTwitterId() {
		return twitterId;
	}

	public String getName() {
		return name;
	}
}