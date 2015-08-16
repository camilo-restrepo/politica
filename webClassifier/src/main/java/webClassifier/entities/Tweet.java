package webClassifier.entities;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("onlyText")
public class Tweet {
	
	@Id private ObjectId _id;
	
	private String id;
	private String text;
	private int classification;
	
	public Tweet() {
	}
	public String getId() {
		return id;
	}
	public String getText() {
		return text;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getClassification() {
		return classification;
	}
	public void setClassification(int classification) {
		this.classification = classification;
	}
}