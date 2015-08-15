package co.inc.board.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class TweetPerDay {

    private final DateTime date;
    private final long tweetsPerDay;

    @JsonCreator
    public TweetPerDay(@JsonProperty("date") DateTime date, @JsonProperty("tweetsPerDay") long tweetsPerDay) {
        this.date = date;
        this.tweetsPerDay = tweetsPerDay;
    }

    public DateTime getDate() {
        return date;
    }

    public long getTweetsPerDay() {
        return tweetsPerDay;
    }
}
