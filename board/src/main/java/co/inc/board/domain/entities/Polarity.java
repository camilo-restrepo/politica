package co.inc.board.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class Polarity {

    private final String twitterId;
    private final long positivePolarity;
    private final long negativePolarity;

    @JsonCreator
    public Polarity(@JsonProperty("twitterId") String twitterId, @JsonProperty("positivePolarity") long positivePolarity,
                    @JsonProperty("negativePolarity") long negativePolarity) {

        this.twitterId = twitterId;
        this.positivePolarity = positivePolarity;
        this.negativePolarity = negativePolarity;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public long getPositivePolarity() {
        return positivePolarity;
    }

    public long getNegativePolarity() {
        return negativePolarity;
    }
}
