package co.inc.twitterStreamCrawler.utils.stemmer;


public class SpanishStemmer extends Stemmer {

	private static final long serialVersionUID = 1L;

	private SpanishStemmerSB stemmer = new SpanishStemmerSB();

	public String stemSB(String str) {
		stemmer.setCurrent(str);
		stemmer.stem();
		return stemmer.getCurrent();
	}

	public String stem(String word) {

		int len = word.length() - 1;

		if (len > 3) {

			word = removeSpanishAccent(word);

			if (word.endsWith("eses")) {
				// corteses -> cortés
				word = word.substring(0, len - 1);
				return word;
			}

			if (word.endsWith("ces")) {
				// dos veces -> una vez
				word = word.substring(0, len - 2);
				word = word + 'z';
				return word;
			}

			if (word.endsWith("os") || word.endsWith("as") || word.endsWith("es")) {
				// ending with -os, -as or -es
				word = word.substring(0, len - 1);
				return word;

			}
			if (word.endsWith("o") || word.endsWith("a") || word.endsWith("e")) {
				// ending with -o, -a, or -e
				word = word.substring(0, len - 1);
				return word;
			}

		}
		return word;
	}

	private String removeSpanishAccent(String word) {
		word = word.replaceAll("à|á|â|ä", "a");
		word = word.replaceAll("ò|ó|ô|ö", "o");
		word = word.replaceAll("è|é|ê|ë", "e");
		word = word.replaceAll("ù|ú|û|ü", "u");
		word = word.replaceAll("ì|í|î|ï", "i");

		return word;
	}
}