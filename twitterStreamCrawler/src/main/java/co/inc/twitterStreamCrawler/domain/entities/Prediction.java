package co.inc.twitterStreamCrawler.domain.entities;

import java.util.List;

public class Prediction {
	
	private final String prediction;
	private final List<Double> ditribution;

	public Prediction(String prediction, List<Double> ditribution) {
		this.prediction = prediction;
		this.ditribution = ditribution;
	}
	
	public String getPrediction() {
		return prediction;
	}
	public List<Double> getDitribution() {
		return ditribution;
	}
}
