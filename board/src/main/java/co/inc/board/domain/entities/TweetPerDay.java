package co.inc.board.domain.entities;

import org.joda.time.DateTime;

public class TweetPerDay {

    private final DateTime date;
    private final long tweetsPerDay;

    public TweetPerDay(DateTime date, long tweetsPerDay) {
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
