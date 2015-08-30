package co.inc.board.persistence.daos;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import co.inc.board.domain.entities.Score;

public class ScoreDAO {

	public static final String TWEETS_COLLECTION = "minimumTweets";

	private final MongoDatabase mongoDatabase;

	public ScoreDAO(MongoDatabase mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
	}

	public List<Score> getTargetScores(String targetId) {
		List<Score> scores = new ArrayList<>();
		int limit = 30;
		DateTime now = DateTime.now().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);

		for (int i = 0; i < limit; i++) {
			DateTime oneDay = now.minusDays(1);
			Bson bson = Filters.and(Filters.eq("targetTwitterId", targetId),
					Filters.gte("timestamp_ms", oneDay.getMillis()), Filters.lte("timestamp_ms", now.getMillis()));

			long count = collection.count(bson);

			double value = (double) count;
			scores.add(new Score(now.getMillis(), value));

			now = oneDay;
		}

		return scores;
	}

}
