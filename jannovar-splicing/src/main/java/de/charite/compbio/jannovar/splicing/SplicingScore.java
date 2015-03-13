package de.charite.compbio.jannovar.splicing;

/**
 * Comparison is done based on the score when compared to the reference.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class SplicingScore implements Comparable<SplicingScore> {

	public enum ScoreType {
		ACCEPTOR, DONOR
	};

	private ScoreType scoreType;
	private final int pos;
	private final int offset;
	private final double refScore;
	private final double altScore;

	public SplicingScore(ScoreType scoreType, int pos, int offset, double refScore, double altScore) {
		this.scoreType = scoreType;
		this.pos = pos;
		this.offset = offset;
		this.refScore = refScore;
		this.altScore = altScore;
	}

	public ScoreType getScoreType() {
		return scoreType;
	}

	public int getPos() {
		return pos;
	}

	public double getRefScore() {
		return refScore;
	}

	public double getAltScore() {
		return altScore;
	}

	@Override
	public String toString() {
		return "SplicingScore [pos=" + pos + ", offset=" + offset + ", scoreType=" + scoreType + ", refScore="
				+ refScore + ", altScore=" + altScore + "]";
	}

	public int compareTo(SplicingScore o) {
		return (int) Math.round(getRefScore() - o.getRefScore());
	}

}
