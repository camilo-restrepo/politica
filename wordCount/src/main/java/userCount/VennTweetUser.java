package userCount;

import java.io.Serializable;

/**
 * Created by Pisco on 11/2/15.
 */
public class VennTweetUser implements Serializable {

    private final Long id;
    private final String userId;

    public VennTweetUser(Long id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VennTweetUser that = (VennTweetUser) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return !(userId != null ? !userId.equals(that.userId) : that.userId != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
