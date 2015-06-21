package crawler.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookUser {
	
	private String id;

	@JsonCreator
	public FacebookUser(@JsonProperty("id") String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
