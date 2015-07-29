package co.inc.twitterStreamCrawler.utils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class TestStanford {
	
	
	private final static StanfordCoreNLP pipeline;
	static{
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		props.setProperty("tokenize.language", "es");
		props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim.tagger");
		pipeline = new StanfordCoreNLP(props);
	}
	
	public List<String> lemmatize(String documentText)
    {
        List<String> lemmas = new LinkedList<String>();
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the list of lemmas
                lemmas.add(token.get(LemmaAnnotation.class));
            }
        }

        return lemmas;
    }
	
	public List<String> posTagging(String documentText)
    {
        List<String> posTagging = new LinkedList<String>();
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);
        // run all Annotators on this text
        this.pipeline.annotate(document);
        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                // Retrieve and add the lemma for each word into the list of lemmas
            	posTagging.add(token.get(PartOfSpeechAnnotation.class));
            }
        }

        return posTagging;
    }
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String content = "@lasillaenvivo: @EnriquePenalosa dice que la diferencia con Rafael Pardo es que a él sí le parece un "
				+ "desastre lo que ha pasado con Bogotá estoy";
		
		TestStanford t = new TestStanford();
//		List<String> result = t.lemmatize(content);
		System.out.println(t.lemmatize(content).toString());
		System.out.println(t.posTagging(content).toString());
	}

}
