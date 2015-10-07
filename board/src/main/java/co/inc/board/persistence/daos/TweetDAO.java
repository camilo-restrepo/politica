package co.inc.board.persistence.daos;

import co.inc.board.domain.entities.MapCoordinate;
import co.inc.board.domain.entities.PredictionEnum;
import co.inc.board.domain.entities.TweetPerDay;
import co.inc.board.domain.entities.TweetsCount;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TweetDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetDAO.class);

	public static final String TWEETS_COLLECTION = "minimumTweets";

	private final MongoDatabase mongoDatabase;

	public TweetDAO(MongoDatabase mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
	}

	public List<MapCoordinate> getMapFromTweetsLastMonth(String twitterId) {

        List<MapCoordinate> mapCoordinates = new ArrayList<MapCoordinate>();

		MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);

        MongoCursor<Document> iterator = collection
                .find(Filters.and(Filters.ne("geo", null), Filters.eq("targetTwitterId", twitterId)))
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

            Bson bson = Filters.and(Filters.eq("targetTwitterId", twitterId),
                    Filters.gte("timestamp_ms", oneDay.getMillis()),
                    Filters.lte("timestamp_ms", now.getMillis()));

            long count = collection.count(bson);

            TweetPerDay tweetPerDay = new TweetPerDay(now, count);
            tweetPerDayList.add(tweetPerDay);
            now = oneDay;
        }

        return tweetPerDayList;
	}

    public long getCandidateTweetsCountByPolarityDateToDay(String twitterId, PredictionEnum predictionValue, DateTime initialDate) {

        MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);

        Bson bson = Filters.and(Filters.eq("targetTwitterId", twitterId),
                Filters.eq("prediction", predictionValue.getValue()),
                Filters.gte("timestamp_ms", initialDate.getMillis()),
                Filters.lte("timestamp_ms", DateTime.now().getMillis()));

        long tweetCountByPolarity = collection.count(bson);

        return tweetCountByPolarity;
    }

    public long getTargetTotalTweets(String twitterId) {

        MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
        return collection.count(Filters.eq("targetTwitterId", twitterId));
    }

    /**
     * Get the number of tweets from a target since a given date to today.
     *
     * @param twitterId The Twitter ID of the target.
     * @param dateTime The initial date to query the tweets.
     *
     * @return the number of tweets from a target since a given date to today.
     */
    public long getTargetTweetsDateToday(String twitterId, DateTime dateTime) {

        MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
        Bson bson = Filters.and(Filters.eq("targetTwitterId", twitterId),
                Filters.gte("timestamp_ms", dateTime.getMillis()),
                Filters.lte("timestamp_ms", DateTime.now().getMillis()));

        return collection.count(bson);
    }

    public TweetsCount getAllTweetsCount(){
        MongoCollection<Document> collection = mongoDatabase.getCollection(TWEETS_COLLECTION);
        return new TweetsCount(collection.count());
    }
}