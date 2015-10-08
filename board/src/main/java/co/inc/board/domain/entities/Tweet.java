package co.inc.board.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Pisco on 10/8/15.
 */
public class Tweet {

    private final String text;
    private final long timestamp_ms;
    private final String targetTwitterId;

    @JsonCreator
    public Tweet(@JsonProperty("text") String text, @JsonProperty("timestamp_ms") long timestamp_ms,
                 @JsonProperty("targetTwitterId") String targetTwitterId, String targetTwitterId1) {
        this.text = text;
        this.timestamp_ms = timestamp_ms;
        this.targetTwitterId = targetTwitterId1;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp_ms() {
        return timestamp_ms;
    }

    public String getTargetTwitterId() {
        return targetTwitterId;
    }
}
