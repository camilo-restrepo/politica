package userCreation;

import java.io.Serializable;

/**
 * Created by Pisco on 10/31/15.
 */
public class CandidateTime implements Serializable{

    private final String target;
    private final Long timestamp;

    public CandidateTime(String target, Long timestamp) {
        this.target = target;
        this.timestamp = timestamp;
    }

    public String getTarget() {
        return target;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CandidateTime that = (CandidateTime) o;

        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        return !(timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null);

    }

    @Override
    public int hashCode() {
        int result = target != null ? target.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
