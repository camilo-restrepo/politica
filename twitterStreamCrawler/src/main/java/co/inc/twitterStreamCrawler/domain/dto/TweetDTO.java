package co.inc.twitterStreamCrawler.domain.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TweetDTO {

	private final String text;
	private final List<String> targets;
	private final long retweets;
	private final long favorites;
	private final long date;
	private final String screenName;
	private final String userId;
	private final String userImageUrl;
	private final int polarity;

	@JsonCreator
	public TweetDTO(@JsonProperty("text") String text, @JsonProperty("targets") List<String> targets,
			@JsonProperty("retweets") long retweets, @JsonProperty("favorites") long favorites, @JsonProperty("date") long date,
			@JsonProperty("screenName") String screenName, @JsonProperty("userId") String userId,
			@JsonProperty("userImageUrl") String userImageUrl, @JsonProperty("polarity") int polarity) {
		this.text = text;
		this.targets = targets;
		this.retweets = retweets;
		this.favorites = favorites;
		this.date = date;
		this.screenName = screenName;
		this.userId = userId;
		this.userImageUrl = userImageUrl;
		this.polarity = polarity;
	}

	public String getText() {
		return text;
	}

	public List<String> getTargets() {
		return targets;
	}

	public long getRetweets() {
		return retweets;
	}

	public long getFavorites() {
		return favorites;
	}

	public long getDate() {
		return date;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserImageUrl() {
		return userImageUrl;
	}
	
	public int getPolarity(){
		return polarity;
	}
}