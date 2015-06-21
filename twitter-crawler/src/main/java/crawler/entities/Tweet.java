package crawler.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tweet {

	private Long twitterId;
	private Long createdAt;
	private Integer favoriteCount;
	private Integer retweetedCount;
	private String text;
	private double latitude;
	private double longitude;
	private TwitterTarget target;
	private TwitterUser user;

	@JsonCreator
	public Tweet(@JsonProperty("twitterId") Long twitterId, @JsonProperty("createdAt") Long createdAt,
			@JsonProperty("favoriteCount") Integer favoriteCount, @JsonProperty("retweetedCount") Integer retweetedCount,
			@JsonProperty("text") String text, @JsonProperty("target") TwitterTarget target,
			@JsonProperty("user") TwitterUser user, @JsonProperty("latitude") double latitude,
			@JsonProperty("longitude") double longitude) {
		this.twitterId = twitterId;
		this.createdAt = createdAt;
		this.favoriteCount = favoriteCount;
		this.retweetedCount = retweetedCount;
		this.text = text;
		this.target = target;
		this.user = user;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Long getTwitterId() {
		return twitterId;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public Integer getFavoriteCount() {
		return favoriteCount;
	}

	public Integer getRetweetedCount() {
		return retweetedCount;
	}

	public String getText() {
		return text;
	}

	public TwitterTarget getTarget() {
		return target;
	}

	public TwitterUser getUser() {
		return user;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
}
