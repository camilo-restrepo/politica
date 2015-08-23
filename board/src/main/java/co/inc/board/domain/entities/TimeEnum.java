package co.inc.board.domain.entities;

/**
 * Created by julian on 8/22/15.
 */
public enum TimeEnum {

    DAY("day"),
    MONTH("month");

    private final String value;

    private TimeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
