package co.inc.board.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Pisco on 10/7/15.
 */
public class TweetsCount {

    private final long count;
    private final long perMinute;

    @JsonCreator
    public TweetsCount(@JsonProperty("count") long count, @JsonProperty("perMinute") long perMinute) {
        this.count = count;
        this.perMinute = perMinute;
    }

    public long getCount() {
        return count;
    }

    public long getPerMinute() {
        return perMinute;
    }
}
