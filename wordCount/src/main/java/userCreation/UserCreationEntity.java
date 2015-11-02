package userCreation;

import java.io.Serializable;

/**
 * Created by Pisco on 10/31/15.
 */
public class UserCreationEntity implements Serializable{

    private final Long id;
    private final Long timestamp;

    public UserCreationEntity(Long id, Long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserCreationEntity that = (UserCreationEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return !(timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
