package co.inc.twitterStreamCrawler.utils.stopwords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

public class StopwordsSpanish extends Stopwords {

	private static final long serialVersionUID = 1L;

	/** The location of the stopwords file **/
	private static String filePath;

	/** The hashtable containing the list of stopwords */
	private static HashSet<String> m_Stopwords = null;

	public StopwordsSpanish(String filePath) {
		super(filePath);
		if (m_Stopwords == null) {
			m_Stopwords = new HashSet<String>();

			File txt = new File(filePath);
			InputStreamReader is;
			String sw = null;
			try {
				is = new InputStreamReader(new FileInputStream(txt), "UTF-8");
				BufferedReader br = new BufferedReader(is);
				while ((sw = br.readLine()) != null) {
					m_Stopwords.add(sw);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns true if the given string is a stop word.
	 */
	public boolean isStopword(String str) {
		return m_Stopwords.contains(str.toLowerCase());
	}
}