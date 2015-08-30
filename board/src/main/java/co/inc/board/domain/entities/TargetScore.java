package co.inc.board.domain.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TargetScore {
	
	private final String target;
	private final List<Score> scores;

	@JsonCreator
	public TargetScore(@JsonProperty("target") String target, @JsonProperty("scores") List<Score> scores) {
		this.target = target;
		this.scores = scores;
	}
	
	public String getTarget() {
		return target;
	}
	
	public List<Score> getScores() {
		return scores;
	}
}
