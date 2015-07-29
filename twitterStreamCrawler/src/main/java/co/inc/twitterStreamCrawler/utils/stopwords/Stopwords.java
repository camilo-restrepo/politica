package co.inc.twitterStreamCrawler.utils.stopwords;

import java.io.Serializable;

public abstract class Stopwords implements Serializable {

	private static final long serialVersionUID = 1L;

	String filePath;

	public Stopwords(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Returns true if the given string is a stop word.
	 */
	public abstract boolean isStopword(String str);
}