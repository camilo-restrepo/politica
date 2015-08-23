package co.inc.board.persistence.daos;

import co.inc.board.domain.entities.MapCoordinate;
import co.inc.board.domain.entities.Polarity;
import co.inc.board.domain.entities.PolarityEnum;
import co.inc.board.domain.entities.TweetPerDay;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class TweetDAO {

	public static final String TWEETS_COLLECTION = "tweets";

	private final MongoDatabase mongoDatabase;

	public TweetDAO(MongoDatabase mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
	}

	public void insertTweet(long targetTwitterId, String stringTweet) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		Document documentTweet = Document.parse(stringTweet);
		documentTweet.put("targetTwitterId", targetTwitterId);
		collection.insertOne(documentTweet);
	}

	public void insertTweet(String stringTweet) {
		Document documentTweet = Document.parse(stringTweet);
		insertTweet(documentTweet);
	}
	
	public void insertTweet(Document documentTweet) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
		collection.insertOne(documentTweet);
	}

	public List<MapCoordinate> getMapFromTweetsLastMonth(String twitterId) {

        List<MapCoordinate> mapCoordinates = new ArrayList<MapCoordinate>();

		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);

        MongoCursor<Document> iterator = collection
                .find(Filters.and(Filters.ne("geo", null), Filters.in("targetTwitterIds", twitterId)))
                .projection(Projections.include("geo")).iterator();

        while (iterator.hasNext()) {

            Document document = iterator.next();
            List<Double> coordinatesList = (List<Double>) document.get("geo.coordinates");
            Double latitude = coordinatesList.get(0);
            Double longitude = coordinatesList.get(1);

            MapCoordinate mapCoordinate = new MapCoordinate(latitude, longitude);
            mapCoordinates.add(mapCoordinate);
        }

        return mapCoordinates;
    }

	public List<TweetPerDay> getTweetsPerDayLastMonth(String twitterId) {

        List<TweetPerDay> tweetPerDayList = new ArrayList<>();

        int limit = 30;
        DateTime now = DateTime.now().withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
        MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);

        for (int i = 0; i < limit; i++) {

            DateTime oneDay = now.minusDays(1);

            long count = collection.count((Filters.and(Filters.in("targetTwitterIds", twitterId),
                    Filters.gte("timestamp_ms", oneDay.getMillis()), Filters.lte("timestamp_ms", now.getMillis()))));

//             MongoCursor<Document> iterator = collection.find().
//                    filter(Filters.and(Filters.in("targetTwitterIds", twitterId),
//                    Filters.gte("timestamp_ms", oneDay), Filters.lte("timestamp_ms", now))).iterator();

            TweetPerDay tweetPerDay = new TweetPerDay(now, count);
            tweetPerDayList.add(tweetPerDay);
            now = oneDay;
        }

        return tweetPerDayList;
	}

    public long getCandidateTweetsCountByPolarityDateToDay(String twitterId, PolarityEnum polarityValue, DateTime initialDate) {

        MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);

        long tweetCountByPolarity = collection.count((Filters.and(Filters.in("targetTwitterIds", twitterId),
                Filters.eq("polarity", polarityValue.getValue()), Filters.gte("timestamp_ms", initialDate.getMillis()),
                Filters.lte("timestamp_ms", DateTime.now().getMillis()))));

        return tweetCountByPolarity;
    }
}