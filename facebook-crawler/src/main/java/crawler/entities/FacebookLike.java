package crawler.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookLike {

	private String facebookId;
	private FacebookUser user;
	private FacebookComment comment;
	private FacebookPost post;

	public FacebookLike(@JsonProperty("facebookId") String facebookId, @JsonProperty("user") FacebookUser user,
			@JsonProperty("comment") FacebookComment comment, @JsonProperty("post") FacebookPost post) {
		this.facebookId = facebookId;
		this.user = user;
		this.comment = comment;
		this.post = post;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public FacebookUser getUser() {
		return user;
	}

	public FacebookComment getComment() {
		return comment;
	}

	public FacebookPost getPost() {
		return post;
	}
}
