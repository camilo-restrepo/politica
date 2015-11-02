package userCount;

import java.io.Serializable;

/**
 * Created by Pisco on 10/30/15.
 */
public class CandidateUser implements Serializable {

    private final String targetTwitterId;
    private final String user;
    private final long createdAt;

    public CandidateUser(String targetTwitterId, String user, long createdAt) {
        this.targetTwitterId = targetTwitterId;
        this.user = user;
        this.createdAt = createdAt;
    }

    public String getTargetTwitterId() {
        return targetTwitterId;
    }

    public String getUser() {
        return user;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CandidateUser that = (CandidateUser) o;

        if (!targetTwitterId.equals(that.targetTwitterId)) return false;
        return user.equals(that.user);

    }

    @Override
    public int hashCode() {
        int result = targetTwitterId.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }
}
