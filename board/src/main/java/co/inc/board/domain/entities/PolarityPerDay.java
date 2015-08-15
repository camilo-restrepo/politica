package co.inc.board.domain.entities;

import org.joda.time.DateTime;

public class PolarityPerDay {

    private final DateTime date;
    private final long positivePolarity;
    private final long negativePolarity;

    public PolarityPerDay(DateTime date, long positivePolarity, long negativePolarity) {
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
