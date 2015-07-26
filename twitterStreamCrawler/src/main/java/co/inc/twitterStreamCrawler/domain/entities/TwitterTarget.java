package co.inc.twitterStreamCrawler.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitterTarget {

	private final TwitterId twitterId;
	private final String screenName;
	private final String profileImageUrl;

	@JsonCreator
	public TwitterTarget(@JsonProperty("twitterId") TwitterId twitterId, @JsonProperty("screenName") String screenName,
			@JsonProperty("profileImageUrl") String profileImageUrl) {
		this.twitterId = twitterId;
		this.screenName = screenName;
		this.profileImageUrl = profileImageUrl;
	}

	public TwitterId getTwitterId() {
		return twitterId;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}
}
