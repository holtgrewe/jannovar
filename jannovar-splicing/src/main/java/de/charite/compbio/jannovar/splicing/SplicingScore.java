package de.charite.compbio.jannovar.splicing;

public class SplicingScore {

	private final double origDonorScore;
	private final double origAcceptorScor;
	private final double donorScore;
	private final double acceptorScore;

	public SplicingScore(double origDonorScore, double origAcceptorScor, double donorScore, double acceptorScore) {
		this.origDonorScore = origDonorScore;
		this.origAcceptorScor = origAcceptorScor;
		this.donorScore = donorScore;
		this.acceptorScore = acceptorScore;
	}

	public double getOrigDonorScore() {
		return origDonorScore;
	}

	public double getOrigAcceptorScor() {
		return origAcceptorScor;
	}

	public double getDonorScore() {
		return donorScore;
	}

	public double getAcceptorScore() {
		return acceptorScore;
	}

	@Override
	public String toString() {
		return "SplicingScore [origDonorScore=" + origDonorScore + ", origAcceptorScor=" + origAcceptorScor
				+ ", donorScore=" + donorScore + ", acceptorScore=" + acceptorScore + "]";
	}

}
