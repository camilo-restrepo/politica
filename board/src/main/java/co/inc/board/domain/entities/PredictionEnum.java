package co.inc.board.domain.entities;

/**
 * Created by julian on 8/22/15.
 */
public enum PredictionEnum {

    POSITIVE("positive"),
    NEUTRAL("neutral"),
    NEGATIVE("negative");
    
    private String value;
    
    private PredictionEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
