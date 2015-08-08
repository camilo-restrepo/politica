import java.io.Serializable;

public class CandidateWord implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String word;
	private final String candidate;

	public CandidateWord(String word, String candidate) {
		this.word = word;
		this.candidate = candidate;
	}

	public String getWord() {
		return word;
	}

	public String getCandidate() {
		return candidate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((candidate == null) ? 0 : candidate.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CandidateWord other = (CandidateWord) obj;
		if (candidate == null) {
			if (other.candidate != null)
				return false;
		}
		if (word == null) {
			if (other.word != null)
				return false;
		}
		if (!candidate.equals(other.candidate))
			return false;
		if (!word.equals(other.word))
			return false;
		return true;
	}
}
