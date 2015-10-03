package co.inc.board.persistence.daos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.inc.board.infrastructure.mongo.MongoUtils;
import com.fasterxml.jackson.databind.MappingIterator;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.inc.board.domain.entities.TwitterId;
import co.inc.board.domain.entities.TwitterTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class TargetDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(TargetDAO.class);

	public static final String IDS_COLLECTION = "twitterIds";
	public static final String TARGETS_COLLECTION = "twitterTargets";

	private final MongoDatabase mongoDatabase;
	private final ObjectMapper objectMapper;

	public TargetDAO(MongoDatabase mongoDatabase, ObjectMapper objectMapper) {
		this.mongoDatabase = mongoDatabase;
		this.objectMapper = objectMapper;
	}

	public List<TwitterId> getAllIds() {
		MongoCollection<Document> collection = mongoDatabase.getCollection(IDS_COLLECTION);
		MongoCursor<Document> it = collection.find().iterator();
		List<TwitterId> ids = new ArrayList<TwitterId>();
		while(it.hasNext()){
			try {
				TwitterId id = objectMapper.readValue(it.next().toJson(), TwitterId.class);
				ids.add(id);
			} catch (IOException e) {
				LOGGER.error("getAllIds", e);
			}
		}
		return ids;
	}

	public TwitterTarget getSingleTarget(String twitterId) {

		TwitterTarget twitterTarget = null;

		try {

			MongoCollection<Document> collection = mongoDatabase.getCollection(TARGETS_COLLECTION);
			Document targetDocument = collection.find(Filters.eq("twitterId.id", twitterId)).first();
			twitterTarget = (!MongoUtils.documentIsBlank(targetDocument)) ?
					objectMapper.readValue(targetDocument.toJson(), TwitterTarget.class) : twitterTarget;

		} catch (IOException e) {

			LOGGER.error("getSingleTarget", e);
		}

		return twitterTarget;
	}

	public List<TwitterTarget> getAllTargets() {

		MongoCollection<Document> collection = mongoDatabase.getCollection(TARGETS_COLLECTION);
		MongoCursor<Document> it = collection.find().iterator();
		List<TwitterTarget> targets = new ArrayList<TwitterTarget>();
		while(it.hasNext()){
			try {
				TwitterTarget target = objectMapper.readValue(it.next().toJson(), TwitterTarget.class);
				targets.add(target);
			} catch (IOException e) {
				LOGGER.error("getAllTargets", e);
			}
		}
		return targets;
	}

	public void insertTwitterTarget(TwitterTarget target) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(TARGETS_COLLECTION);
		try {
			String targetJson = objectMapper.writeValueAsString(target);
			Document targetDocument = Document.parse(targetJson);
			collection.insertOne(targetDocument);
		} catch (JsonProcessingException e) {
			LOGGER.error("insertTwitterTarget", e);
		}
	}
}
