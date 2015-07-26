package co.inc.board.domain.workers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import co.inc.board.persistence.daos.TweetDAO;

public class TwitterConsumerWorker implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterConsumerWorker.class);

	private final String stringTweet;
	private final TweetDAO tweetDAO;

	public TwitterConsumerWorker(TweetDAO tweetDAO, String stringTweet) {
		this.tweetDAO = tweetDAO;
		this.stringTweet = stringTweet;
	}

	@Override
	public void run() {
		Document documentTweet = Document.parse(stringTweet);
		String tweetText = (String) documentTweet.get("text");
		List<String> targets = new ArrayList<String>();
		targets.add("cvderoux");
		targets.add("ClaraLopezObre");
		targets.add("rafaelpardo");
		targets.add("enriquepenalosa");
		targets.add("mmmaldonadoc");
		targets.add("ricardoariasm");
		targets.add("PachoSantosC");
		targets.add("HollmanMorris");
		targets.add("AlexVernot");
		targets.add("danielraisbeck");

		Hashtable<String, List<String>> targetsTerms = new Hashtable<String, List<String>>();
		targetsTerms.put("cvderoux",
				Lists.newArrayList("Carlos Vicente De Roux", "Carlos Vicente de Roux", "cvderoux", "@cvderoux"));
		targetsTerms.put("ClaraLopezObre",
				Lists.newArrayList("Clara Lopez Obregon", "Clara Lopez", "ClaraLopezObre", "@ClaraLopezObre"));
		targetsTerms.put("rafaelpardo", Lists.newArrayList("Rafael Pardo", "rafaelpardo", "@rafaelpardo"));
		targetsTerms.put("enriquepenalosa", Lists.newArrayList("Enrique Penalosa", "Enrique Peñalosa", "enriquepenalosa",
				"equipoporbogota", "@enriquepenalosa"));
		targetsTerms.put("mmmaldonadoc", Lists.newArrayList("Maria Mercedes Maldonado", "mmmaldonadoc", "@mmmaldonadoc"));
		targetsTerms.put("ricardoariasm", Lists.newArrayList("Ricardo Arias Mora", "ricardoariasm", "@ricardoariasm"));
		targetsTerms.put("PachoSantosC", Lists.newArrayList("Francisco Santos", "Pacho Santos", "Pachito Santos", "PachoSantosC",
				"CambioConSeguridad", "@PachoSantosC"));
		targetsTerms.put("HollmanMorris", Lists.newArrayList("Hollman Morris", "HollmanMorris", "HollmanMorris"));
		targetsTerms.put("AlexVernot", Lists.newArrayList("Alex Vernot", "AlexVernot", "@AlexVernot"));
		targetsTerms.put("danielraisbeck", Lists.newArrayList("Daniel Raisbeck", "danielraisbeck", "@danielraisbeck"));

		List<String> foundtargets = new ArrayList<String>();
		for(String target : targets){
			List<String> relatedWords = targetsTerms.get(target);
			for(String word : relatedWords){
				if(tweetText.toLowerCase().contains(word.toLowerCase())){
					foundtargets.add(target);
					break;
				}
			}
		}
		
		documentTweet.append("targetTwitterIds", foundtargets);
		tweetDAO.insertTweet(documentTweet);
	}
}