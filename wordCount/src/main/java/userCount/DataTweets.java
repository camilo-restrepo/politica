package userCount;

import java.io.Serializable;

/**
 * Created by Pisco on 10/30/15.
 */
public class DataTweets implements Serializable {

    private final String user;
    private final long id;
    private final long createdAt;

    public DataTweets(String user, long id, long createdAt) {
        this.user = user;
        this.id = id;
        this.createdAt = createdAt;
    }

    public String getUser() {
        return user;
    }

    public long getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataTweets that = (DataTweets) o;

        if (id != that.id) return false;
        return !(user != null ? !user.equals(that.user) : that.user != null);

    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "DataTweets{" +
                "user='" + user + '\'' +
                ", id=" + id +
                '}';
    }
}
