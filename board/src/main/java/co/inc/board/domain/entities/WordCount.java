package co.inc.board.domain.entities;

public class WordCount {

    private final String word;
    private final int count;

    public WordCount(String word, int count) {
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
