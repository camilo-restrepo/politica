package co.inc.twitterStreamCrawler.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import co.inc.twitterStreamCrawler.utils.stopwords.StopwordsSpanish;

public class PolarityClassifier {

	private Hashtable<String, List<String>> englishDictionary;
	private Hashtable<String, List<Polarity>> englishPolarities;
	private Set<String> spanishWords;
	private Set<String> englishWords;

	private final String stopwordsFile;

	public PolarityClassifier(String nrcFile, String translateFile, String stopwordsFile) {
		this.stopwordsFile = stopwordsFile;
		englishDictionary = new Hashtable<String, List<String>>();
		englishPolarities = new Hashtable<String, List<Polarity>>();
		spanishWords = new HashSet<String>();
		englishWords = new HashSet<String>();
		try {
			loadEnglishTranslation(translateFile);
			loadEnglishPolarities(nrcFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadEnglishPolarities(String nrcFile) throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(new File(nrcFile)));
		String str = bf.readLine();
		str = bf.readLine();
		while (str != null) {
			String[] linea = str.split("\t");
			String word = linea[0];
			String category = linea[1];
			double probability = Double.parseDouble(linea[2]);
			englishWords.add(word);
			if (englishPolarities.containsKey(word)) {
				List<Polarity> polarities = englishPolarities.get(word);
				polarities.add(new Polarity(word, probability, category));
				englishPolarities.put(word, polarities);
			} else {
				List<Polarity> polarities = new ArrayList<Polarity>();
				polarities.add(new Polarity(word, probability, category));
				englishPolarities.put(word, polarities);
			}
			str = bf.readLine();
		}
		bf.close();
	}

	private void loadEnglishTranslation(String translateFile) throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(new File(translateFile)));
		String str = bf.readLine();
		str = bf.readLine();
		while (str != null) {
			String[] linea = str.split(",");
			String english = linea[0];
			String spanish = linea[1];
			spanishWords.add(spanish);
			englishWords.add(english);
			if (englishDictionary.containsKey(spanish)) {
				List<String> translations = englishDictionary.get(spanish);
				translations.add(english);
				englishDictionary.put(spanish, translations);
			} else {
				List<String> translations = new ArrayList<String>();
				translations.add(english);
				englishDictionary.put(spanish, translations);
			}
			str = bf.readLine();
		}
		bf.close();
	}

	private List<Token> getTweetTokens(String tweet) {
		List<Token> tokens = new ArrayList<Token>();
		tweet = removeSpanishAccent(tweet);
		tweet = tweet.replace(".", "").replace(",", "").replace(":", "").trim().toLowerCase();
		String[] tweetTokens = tweet.split(" +");
		StopwordsSpanish stopwords = new StopwordsSpanish(stopwordsFile);
		for (String token : tweetTokens) {
			if (!token.startsWith("@") && !token.startsWith("http") && !stopwords.isStopword(token)) {
				tokens.add(new Token(token, 1.0));
				List<String> similarities = getSimilarities(token);
				for (int j = 0; j < similarities.size(); j++) {
					tokens.add(new Token(similarities.get(j), 0.6));
				}
			}
		}
		return tokens;
	}

	private List<String> getSimilarities(String token) {
		Set<String> similarities = new HashSet<String>();
		for (String word : spanishWords) {
			if (FuzzyMatch.getRatio(word, token, false) > 80) {
				similarities.add(word);
			}
		}
//		System.out.println(token + ": " + similarities.toString());
		return new ArrayList<String>(similarities);
	}

	private ArrayList<Token> translateTokens(String tweet) {
		Set<Token> translatedTokens = new HashSet<Token>();
		List<Token> tokens = getTweetTokens(tweet);
		for (Token token : tokens) {
			if (englishDictionary.containsKey(token.getToken())) {
				List<String> englishTokens = englishDictionary.get(token.getToken());
				for (int i = 0; i < englishTokens.size(); i++) {
					translatedTokens.add(new Token(englishTokens.get(i), token.getWeight()));
				}
			}
		}
		return new ArrayList<Token>(translatedTokens);
	}

	private List<Polarity> getTweetPolarities(String tweet) {
		List<Token> translatedTokens = translateTokens(tweet);
		List<Polarity> polarities = new ArrayList<Polarity>();
		for (Token token : translatedTokens) {
			if (englishPolarities.containsKey(token.getToken())) {
				List<Polarity> actualPolarities = englishPolarities.get(token.getToken());
				if(actualPolarities != null){
					for (int i = 0; i < actualPolarities.size(); i++) {
						polarities.add(new Polarity(actualPolarities.get(i).getWord(), token.getWeight(),
								actualPolarities.get(i).getCategory()));
					}					
				}
			}
		}

		polarities = polarities.parallelStream().filter(p -> Double.compare(p.getProbability(), 0.0D) != 0)
				.collect(Collectors.toList());
		return polarities;
	}

	public int getTweetPolarity(String tweet) {
		List<Polarity> polarities = getTweetPolarities(tweet);

		double positiveCount = 0;
		double negativeCount = 0;
		for (Polarity polarity : polarities) {
			System.out.println(polarity);
			if (polarity.getCategory().equals("joy") || polarity.getCategory().equals("positive")) {
				positiveCount += polarity.getProbability();
			} else if (polarity.getCategory().equals("anger") || polarity.getCategory().equals("fear")
					|| polarity.getCategory().equals("disgust") || polarity.getCategory().equals("sadness")
					|| polarity.getCategory().equals("negative")) {
				negativeCount += polarity.getProbability();
			}
		}
		System.out.println(negativeCount + " - " + positiveCount);
		if (negativeCount > positiveCount) {
			return -1;
		} else if (positiveCount > negativeCount) {
			return 1;
		} else {
			return 0;
		}
	}

	public static String removeSpanishAccent(String word) {
		word = word.replaceAll("à|á|â|ä", "a");
		word = word.replaceAll("ò|ó|ô|ö", "o");
		word = word.replaceAll("è|é|ê|ë", "e");
		word = word.replaceAll("ù|ú|û|ü", "u");
		word = word.replaceAll("ì|í|î|ï", "i");

		return word;
	}
	
	public static void main(String[] args) {
		PolarityClassifier p = new PolarityClassifier("./data/NRC.txt", "./data/Translate.csv", "./data/stopwords_es.txt");
		String tweet = "Para mí el único que tiene una apuesta política coherente, renovadora y equilibrada es @CVderoux. Ahí les dejo el pendiente.";
		p.getTweetPolarity(tweet);
	}
}
