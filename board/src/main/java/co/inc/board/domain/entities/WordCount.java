package co.inc.board.domain.entities;

public class WordCount {

    private final String word;
    private final long count;

    public WordCount(String word, long count) {
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public long getCount() {
        return count;
    }
}
