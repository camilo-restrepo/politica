package co.inc.board.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class PolarityPerDay {

    private final DateTime date;
    private final long positivePolarity;
    private final long negativePolarity;

    @JsonCreator
    public PolarityPerDay(@JsonProperty("date") DateTime date, @JsonProperty("positivePolarity") long positivePolarity,
                          @JsonProperty("negativePolarity")long negativePolarity) {
        this.date = date;
        this.positivePolarity = positivePolarity;
        this.negativePolarity = negativePolarity;
    }

    public DateTime getDate() {
        return date;
    }

    public long getPositivePolarity() {
        return positivePolarity;
    }

    public long getNegativePolarity() {
        return negativePolarity;
    }
}
