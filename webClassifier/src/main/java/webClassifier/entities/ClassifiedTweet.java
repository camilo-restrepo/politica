package webClassifier.entities;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("classifiedTweets")
public class ClassifiedTweet {
	
	@Id private ObjectId _id;
	
	private long id;
	private String text;
	private int polarity;
	private int classification;
	
	public ClassifiedTweet() {
	}
	public long getId() {
		return id;
	}
	public String getText() {
		return text;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getPolarity() {
		return polarity;
	}
	public void setPolarit(int polarity){
		this.polarity = polarity;
	}
	public int getClassification() {
		return classification;
	}
	public void setClassification(int classification) {
		this.classification = classification;
	}
	public void setPolarity(int polarity) {
		this.polarity = polarity;
	}
	
}