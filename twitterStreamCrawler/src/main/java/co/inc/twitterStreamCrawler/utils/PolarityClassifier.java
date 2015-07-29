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

	public PolarityClassifier() {
		englishDictionary = new Hashtable<String, List<String>>();
		englishPolarities = new Hashtable<String, List<Polarity>>();
		spanishWords = new HashSet<String>();
		englishWords = new HashSet<String>();
		try {
			loadEnglishTranslation();
			loadEnglishPolarities();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadEnglishPolarities() throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(new File("./data/NRC.txt")));
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

	private void loadEnglishTranslation() throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(new File("./data/translate.csv")));
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

	private List<String> getTweetTokens(String tweet) {
		List<String> tokens = new ArrayList<String>();
		tweet = removeSpanishAccent(tweet);
		tweet = tweet.replace(".", "").replace(",", "").replace(":", "").trim().toLowerCase();
		String[] tweetTokens = tweet.split(" +");
		StopwordsSpanish stopwords = new StopwordsSpanish("./data/stopwords_es.txt");
		for (String token : tweetTokens) {
			if (!token.startsWith("@") && !token.startsWith("http") && !stopwords.isStopword(token)) {
				tokens.add(token);
				tokens.addAll(getSimilarities(token));
			}
		}
		return tokens;
	}

	private List<String> getSimilarities(String token) {
		Set<String> similarities = new HashSet<String>();
		for(String word : spanishWords){
			if(FuzzyMatch.getRatio(word, token, false) > 80){
				similarities.add(word);
			}
		}
//		System.out.println(token + ": " + similarities.toString());
		return new ArrayList<String>(similarities);
	}

	private List<String> translateTokens(String tweet) {
		Set<String> translatedTokens = new HashSet<String>();
		List<String> tokens = getTweetTokens(tweet);
		for (String token : tokens) {
			if (englishDictionary.containsKey(token)) {
				translatedTokens.addAll(englishDictionary.get(token));
			}
		}
		return new ArrayList<String>(translatedTokens);
	}

	private List<Polarity> getTweetPolarities(String tweet) {
		List<String> translatedTokens = translateTokens(tweet);
		List<Polarity> polarities = new ArrayList<Polarity>();
		for (String token : translatedTokens) {
			if (englishPolarities.containsKey(token)) {
				polarities.addAll(englishPolarities.get(token));
			}
		}

		polarities = polarities.parallelStream().filter(p -> Double.compare(p.getProbability(), 0.0D) != 0)
				.collect(Collectors.toList());
		return polarities;
	}

	public int getTweetPolarity(String tweet){
		List<Polarity> polarities = getTweetPolarities(tweet);
		
		int positiveCount = 0;
		int negativeCount = 0;
		for(Polarity polarity : polarities){
//			System.out.println(polarity);
			if(polarity.getCategory().equals("joy") || polarity.getCategory().equals("positive")){
				positiveCount++;
			}else if(polarity.getCategory().equals("anger") || polarity.getCategory().equals("fear") || 
					polarity.getCategory().equals("disgust") || polarity.getCategory().equals("sadness") || 
					polarity.getCategory().equals("negative")){
				negativeCount++;
			}
		}
		
		if(negativeCount>positiveCount){
			return -1;			
		}else if(positiveCount>negativeCount){
			return 1;	
		}else{
			return 0;	
		}
	}

	public static void main(String[] args) throws IOException {
		PolarityClassifier polarityClassifier = new PolarityClassifier();

		String[] tweets = {
				"@gusgomez1701 @AlejaRodC @CVderoux Gustavo, no apoye a ninguno, la política perjudica hasta a los más nobles.",
				"RT @elespectador: Carlos Vicente de Roux, candidato oficial de Alianza Verde para la Alcaldía de Bogotá http://t.co/wx6JPz5yjb http://t.co/…",
				"RT @CVderoux: Acabo de suscribir, en señal de aceptación, el aval de Alianza Verde a mi candidatura a la Alcaldía de Bogotá http://t.co/tcq…",
				"@lasillaenvivo: @EnriquePenalosa dice que la diferencia con Rafael Pardo es que a él sí le parece un desastre lo que ha pasado con Bogotá" };

		for (String tweet : tweets) {
			System.out.println(tweet);
			System.out.println(polarityClassifier.getTweetPolarity(tweet));
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
}
