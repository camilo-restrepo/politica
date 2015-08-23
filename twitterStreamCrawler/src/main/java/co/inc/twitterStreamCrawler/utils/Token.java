package co.inc.twitterStreamCrawler.utils;

public class Token {
	
	private final String token;
	private final double weight;
	
	public Token(final String token, final double weight) {
		this.token = token;
		this.weight = weight;
	}

	public String getToken() {
		return token;
	}

	public double getWeight() {
		return weight;
	}
}