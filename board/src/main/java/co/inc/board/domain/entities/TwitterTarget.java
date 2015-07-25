package co.inc.board.domain.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitterTarget {

	private final long twitterId;
	private final String screenName;
	private final List<String> relatedWords;

	@JsonCreator
	public TwitterTarget(@JsonProperty("twitterId") long twitterId, @JsonProperty("screenName") String screenName,
			@JsonProperty("relatedWords") List<String> relatedWords) {
		this.twitterId = twitterId;
		this.screenName = screenName;
		this.relatedWords = relatedWords;
	}

	public long getTwitterId() {
		return twitterId;
	}

	public String getScreenName() {
		return screenName;
	}

	public List<String> getRelatedWords() {
		return relatedWords;
	}

}
