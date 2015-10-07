package co.inc.board.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Pisco on 10/7/15.
 */
public class TweetsCount {

    private final long count;

    @JsonCreator
    public TweetsCount(@JsonProperty("count") long count) {
        this.count = count;
    }

    public long getCount() {
        return count;
    }
}
