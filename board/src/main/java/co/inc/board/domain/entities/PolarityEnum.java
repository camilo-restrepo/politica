package co.inc.board.domain.entities;

/**
 * Created by julian on 8/22/15.
 */
public enum PolarityEnum {

    POSITIVE(1), 
    NEGATIVE(-1);
    
    private int value;
    
    private PolarityEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
