package co.inc.board.domain.entities;

import java.util.List;

public class CloudTag {

    private final String twitterId;
    private final List<WordCount> wordCountList;

    public CloudTag(String twitterId, List<WordCount> wordCountList) {
        this.twitterId = twitterId;
        this.wordCountList = wordCountList;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public List<WordCount> getWordCountList() {
        return wordCountList;
    }
}
