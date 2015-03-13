package de.charite.compbio.jannovar.splicing;

public class Rogan2003SplicingDonorMatrix extends MatrixData {

	private static double[][] columns = { { 0.283450, 0.396834, -0.568674, -1.220276 },
			{ 1.229685, -1.397305, -1.276078, -1.001612 }, { -1.456455, -3.334596, 1.551773, -2.047699 },
			{ -15.780540, -15.780540, 1.862810, -15.780540 }, { -15.780540, -5.149494, -15.780540, 1.851591 },
			{ 1.123510, -3.416837, 0.340151, -3.338363 }, { 1.349145, -1.915567, -1.266851, -1.290145 },
			{ -1.679871, -2.346400, 1.511565, -1.850917 }, { -0.617326, -0.913107, -0.517867, 0.812155 },
			{ 0.111954, -0.523082, 0.098607, -0.340368 }, };

	@Override
	protected double[] getWeights(int pos) {
		return columns[pos - getMinOffset()];
	}

	@Override
	public int getMinOffset() {
		return -3;
	}

	@Override
	public int getMaxOffset() {
		return 6;
	}

}
