package crawler.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookPage {

	private String facebookId;
	private String name;
	private Long likes;
	private Long timestamp;

	@JsonCreator
	public FacebookPage(@JsonProperty("facebookId") String facebookId, @JsonProperty("name") String name,
			@JsonProperty("likes") Long likes, @JsonProperty("timestamp") Long timestamp) {
		this.facebookId = facebookId;
		this.name = name;
		this.likes = likes;
		this.timestamp = timestamp;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public String getName() {
		return name;
	}

	public Long getLikes() {
		return likes;
	}

	public Long getTimestamp() {
		return timestamp;
	}
}