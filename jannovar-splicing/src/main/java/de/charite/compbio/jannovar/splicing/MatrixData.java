package de.charite.compbio.jannovar.splicing;

// TODO(holtgrewe): Split into data and operation class.

public abstract class MatrixData {

	/** @return weight multiplied by nucleotide at the given position. */
	public double getWeight(int pos, char nt) {
		int idx = ntToIdx(nt);
		if (idx == -1)
			return 0.0;
		return getWeights(pos)[idx];
	}

	/**
	 * @param seq
	 *            sequence to shift matrix over
	 * @param offset
	 *            offset in sequence to let matrix start at
	 * @return dot-product of matrix with the given sequence
	 */
	public double getScore(String seq, int offset) {
		double sum = 0;
		for (int pos = offset, i = getMinOffset(); i <= getMaxOffset(); ++i, ++pos)
			sum += getWeight(i, seq.charAt(pos));
		return sum;
	}

	/** @return weight vector for the given position */
	protected abstract double[] getWeights(int pos);

	/** @return minimal offset from considered point, -50 for Schneider matrices */
	public abstract int getMinOffset();

	/** @return maximal offset from considered point, 50 for Schneider matrices */
	public abstract int getMaxOffset();

	/** @return length of matrix */
	public int length() {
		return getMaxOffset() - getMinOffset() + 1;
	}

	/** @return convert A, C, G, T to index in vector */
	public static int ntToIdx(char nt) {
		switch (nt) {
		case 'A':
		case 'a':
			return 0;
		case 'C':
		case 'c':
			return 1;
		case 'G':
		case 'g':
			return 2;
		case 'T':
		case 't':
			return 3;
		default:
			return -1;
		}
	}

}
