package crawler.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookTarget {

	private String id;

	public FacebookTarget(){
		
	}
	
	@JsonCreator
	public FacebookTarget(@JsonProperty("id") String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
