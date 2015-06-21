package crawler.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookComment {

	private String facebookId;
	private String message;
	private Long likesCount;
	private Long timestamp;
	private FacebookPost post;

	@JsonCreator
	public FacebookComment(@JsonProperty("facebookId") String facebookId, @JsonProperty("message") String message,
			@JsonProperty("likesCount") Long likesCount, @JsonProperty("timestamp") Long timestamp,
			@JsonProperty("post") FacebookPost post) {
		this.facebookId = facebookId;
		this.message = message;
		this.likesCount = likesCount;
		this.timestamp = timestamp;
		this.post = post;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public String getMessage() {
		return message;
	}

	public Long getLikesCount() {
		return likesCount;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public FacebookPost getPost() {
		return post;
	}
}
