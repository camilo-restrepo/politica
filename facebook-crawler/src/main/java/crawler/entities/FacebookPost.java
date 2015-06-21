package crawler.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookPost {

	private String facebookId;
	private String message;
	private Long commentsCount;
	private Long likesCount;
	private Long sharesCount;
	private Long timestamp;
	private FacebookPage page;

	@JsonCreator
	public FacebookPost(@JsonProperty("facebookId") String facebookId, @JsonProperty("message") String message,
			@JsonProperty("commentsCount") Long commentsCount, @JsonProperty("likesCount") Long likesCount,
			@JsonProperty("sharesCount") Long sharesCount, @JsonProperty("timestamp") Long timestamp,
			@JsonProperty("page") FacebookPage page) {
		this.facebookId = facebookId;
		this.message = message;
		this.commentsCount = commentsCount;
		this.likesCount = likesCount;
		this.sharesCount = sharesCount;
		this.timestamp = timestamp;
		this.page = page;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public String getMessage() {
		return message;
	}

	public Long getCommentsCount() {
		return commentsCount;
	}

	public Long getLikesCount() {
		return likesCount;
	}

	public Long getSharesCount() {
		return sharesCount;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public FacebookPage getPage() {
		return page;
	}
}