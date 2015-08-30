package co.inc.board.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TweetStats {

    /**
     * The target Twitter ID.
     */
    private final String twitterId;

    /**
     * The total number of tweets registered by the crawler since the beginning of the campaign.
     */
    private final long totalTweets;

    /**
     * Tweets that mention the target today.
     */
    private final long tweetsToday;

    /**
     * Tweets that mention the target in the last hour.
     */
    private final long tweetsLastHour;

    @JsonCreator
    public TweetStats(@JsonProperty("twitterId") String twitterId, @JsonProperty("totalTweets") long totalTweets,
                      @JsonProperty("tweetsToday") long tweetsToday, @JsonProperty("tweetsLastHour") long tweetsLastHour) {
        this.twitterId = twitterId;
        this.totalTweets = totalTweets;
        this.tweetsToday = tweetsToday;
        this.tweetsLastHour = tweetsLastHour;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public long getTotalTweets() {
        return totalTweets;
    }

    public long getTweetsToday() {
        return tweetsToday;
    }

    public long getTweetsLastHour() {
        return tweetsLastHour;
    }
}
