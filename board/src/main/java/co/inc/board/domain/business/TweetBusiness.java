package co.inc.board.domain.business;

import co.inc.board.domain.entities.*;
import co.inc.board.persistence.daos.TweetDAO;
import org.joda.time.DateTime;

import java.util.ArrayList;
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

    public Polarity getCandidatePolarityToday(String twitterId) {

        DateTime todayAtZeroZero = DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withMinuteOfHour(0);
        long positiveTweets = tweetDAO.getCandidateTweetsCountByPolarityDateToDay(twitterId, PredictionEnum.POSITIVE, todayAtZeroZero);
        long negativeTweets = tweetDAO.getCandidateTweetsCountByPolarityDateToDay(twitterId, PredictionEnum.NEGATIVE, todayAtZeroZero);

        return new Polarity(twitterId, positiveTweets, negativeTweets);
    }

    public Polarity getCandidatePolarityMonth(String twitterId) {

        DateTime todayAtZeroZero = DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withMinuteOfHour(0);
        DateTime lastMonth = todayAtZeroZero.minusDays(30);
        long positiveTweets = tweetDAO.getCandidateTweetsCountByPolarityDateToDay(twitterId, PredictionEnum.POSITIVE, lastMonth);
        long negativeTweets = tweetDAO.getCandidateTweetsCountByPolarityDateToDay(twitterId, PredictionEnum.NEGATIVE, lastMonth);

        return new Polarity(twitterId, positiveTweets, negativeTweets);
    }

    public List<Polarity> getAllTargetsPolarityToday(List<TwitterTarget> allTargets) {

        List<Polarity> polarityListToday = new ArrayList<>();

        for (TwitterTarget target : allTargets) {

            Polarity candidatePolarityToday = getCandidatePolarityToday(target.getTwitterId().getId());
            polarityListToday.add(candidatePolarityToday);
        }

        return polarityListToday;
    }

    public List<Polarity> getAllTargetsPolarityLastMonth(List<TwitterTarget> allTargets) {

        List<Polarity> polarityListMonth = new ArrayList<>();

        for (TwitterTarget target : allTargets) {

            Polarity candidatePolarityMonth = getCandidatePolarityMonth(target.getTwitterId().getId());
            polarityListMonth.add(candidatePolarityMonth);
        }

        return polarityListMonth;
    }

    public TweetStats getTweetStats(String twitterId) {

        long totalTweets = tweetDAO.getTargetTotalTweets(twitterId);

        DateTime todayAtZeroZero = DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
        long tweetsToday = tweetDAO.getTargetTweetsDateToday(twitterId, todayAtZeroZero);

        DateTime todayOneHourBefore = DateTime.now().minusHours(1);
        long tweetsLastHour = tweetDAO.getTargetTweetsDateToday(twitterId, todayOneHourBefore);

        return new TweetStats(twitterId, totalTweets, tweetsToday, tweetsLastHour);
    }

    public TweetsCount getAllTweetsCount(){
        return tweetDAO.getAllTweetsCount();
    }

    public List<String> getLastTweetsCandidate(String twitterId) {
        return tweetDAO.getLastTweetsCandidate(twitterId);

    }

    public List<String> getLastTweetsPolarity(String prediction){
        return tweetDAO.getLastTweetsPolarity(prediction);
    }

    public List<List<Double>> getTweetsLocation(){
        return tweetDAO.getTweetsLocation();
    }
}
