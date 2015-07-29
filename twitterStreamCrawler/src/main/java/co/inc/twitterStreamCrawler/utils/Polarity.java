package co.inc.twitterStreamCrawler.utils;

public class Polarity {

	private final String word;
	private final double probability;
	private final String category;

	public Polarity(String word, double probability, String category) {
		this.word = word;
		this.probability = probability;
		this.category = category;
	}

	public double getProbability() {
		return probability;
	}

	public String getCategory() {
		return category;
	}

	public String getWord() {
		return word;
	}

	public String toString() {
		return word + " - " + category + " - " + probability;
	}
}
