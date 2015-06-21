package crawler.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitterTarget {
	
	private String id;

	@JsonCreator
	public TwitterTarget(@JsonProperty("id") String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
