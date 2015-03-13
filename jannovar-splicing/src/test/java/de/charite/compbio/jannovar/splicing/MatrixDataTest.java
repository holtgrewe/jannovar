package de.charite.compbio.jannovar.splicing;

import org.junit.Assert;
import org.junit.Test;

public class MatrixDataTest {

	/**
	 * Simple test driver class implementing MatrixData
	 *
	 * <pre>
	 *   0.1  -0.1  0.15
	 *   0.4  -0.4  0.1
	 *   0.4  -0.4  0.1
	 *   0.8  -0.8  0.1
	 * </pre>
	 */
	static class TestMatrixData extends MatrixData {

		private static double[][] columns = { { 0.1, 0.4, 0.4, 0.8 }, { -0.1, -0.4, -0.4, -0.8 },
				{ 0.15, 0.1, 0.1, 0.1 }, };

		@Override
		protected double[] getWeights(int pos) {
			return columns[pos - getMinOffset()];
		}

		@Override
		public int getMinOffset() {
			return -1;
		}

		@Override
		public int getMaxOffset() {
			return 1;
		}

	}

	private final TestMatrixData data = new TestMatrixData();

	@Test
	public void test() {
		Assert.assertEquals(-1, data.getMinOffset());
		Assert.assertEquals(1, data.getMaxOffset());
		Assert.assertEquals(0.15, data.getScore("CGAT", 0), 0.0);
		Assert.assertEquals(0.4, data.getScore("CGAT", 1), 0.0);
	}

}
