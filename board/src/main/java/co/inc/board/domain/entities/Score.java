package co.inc.board.domain.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Score {

	private final long date;
	private final double value;

	public Score(@JsonProperty("date") long date, @JsonProperty("value") double value) {
		this.date = date;
		this.value = value;
	}
	public long getDate() {
		return date;
	}
	public double getValue() {
		return value;
	}
}	
