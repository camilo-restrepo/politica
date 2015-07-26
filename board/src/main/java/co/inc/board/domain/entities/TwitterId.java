package co.inc.board.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitterId {
	
	private final String id;
	
	@JsonCreator
	public TwitterId(@JsonProperty("id") String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}