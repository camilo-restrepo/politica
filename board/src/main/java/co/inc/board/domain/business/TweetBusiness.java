package co.inc.board.domain.business;

import co.inc.board.domain.entities.MapCoordinate;
import co.inc.board.domain.entities.PolarityPerDay;
import co.inc.board.domain.entities.TweetPerDay;
import co.inc.board.persistence.daos.TweetDAO;

import java.util.List;

/**
 * Created by julian on 8/14/15.
 */
public class TweetBusiness {

    private final TweetDAO tweetDAO;

    public TweetBusiness(TweetDAO tweetDAO) {
        this.tweetDAO = tweetDAO;
    }

    public List<MapCoordinate> getMapFromTweetsLastMonth(String twitterId) {

        return tweetDAO.getMapFromTweetsLastMonth(twitterId);
    }

    public List<TweetPerDay> getTweetsPerDayLastMonth(String twitterId) {

        return tweetDAO.getTweetsPerDayLastMonth(twitterId);
    }

    public List<PolarityPerDay> getPolarityLastMonth(String twitterId) {

        return tweetDAO.getPolarityLastMonth(twitterId);
    }
}
