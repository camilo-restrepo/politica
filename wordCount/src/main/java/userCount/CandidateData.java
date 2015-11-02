package userCount;

/**
 * Created by Pisco on 10/30/15.
 */
public class CandidateData {

    private final long id;
    private final String candidate;

    public CandidateData(long id, String candidate) {
        this.id = id;
        this.candidate = candidate;
    }

    public long getId() {
        return id;
    }

    public String getCandidate() {
        return candidate;
    }
}
