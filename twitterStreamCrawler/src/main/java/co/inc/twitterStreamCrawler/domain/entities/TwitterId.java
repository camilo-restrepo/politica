package co.inc.twitterStreamCrawler.domain.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitterId {
	
	private final String id;
	private final List<String> relatedWords;
	
	@JsonCreator
	public TwitterId(@JsonProperty("id") String id, @JsonProperty("relatedWords") List<String> relatedWords) {
		this.id = id;
		this.relatedWords = relatedWords;
	}

	public String getId() {
		return id;
	}
	
	public List<String> getRelatedWords() {
		return relatedWords;
	}
}