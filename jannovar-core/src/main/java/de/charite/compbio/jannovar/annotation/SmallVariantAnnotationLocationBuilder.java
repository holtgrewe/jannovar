package de.charite.compbio.jannovar.annotation;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.SmallVariantAnnotationLocation.RankType;
import de.charite.compbio.jannovar.reference.TranscriptInterval;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Builder for the immutable {@link SmallVariantAnnotationLocation} class.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public class SmallVariantAnnotationLocationBuilder {

	/** {@link SmallVariantAnnotationLocation#transcript} of next build {@link SmallVariantAnnotationLocation}. */
	public TranscriptModel transcript = null;

	/** {@link SmallVariantAnnotationLocation#rankType} of next build {@link SmallVariantAnnotationLocation}. */
	public RankType rankType = RankType.UNDEFINED;

	/** {@link SmallVariantAnnotationLocation#rank} of next build {@link SmallVariantAnnotationLocation}. */
	public int rank = SmallVariantAnnotationLocation.INVALID_RANK;

	// TODO(holtgrem): transcript location probably does not belong here!
	/** {@link SmallVariantAnnotationLocation#txLocation} of next build {@link SmallVariantAnnotationLocation}. */
	public TranscriptInterval txLocation = null;

	/**
	 * @return {@link SmallVariantAnnotationLocation} from the builder's state.
	 */
	public SmallVariantAnnotationLocation build() {
		int totalRank = -1;
		if (rankType == RankType.EXON)
			totalRank = transcript.getExonRegions().size();
		else if (rankType == RankType.INTRON)
			totalRank = transcript.getExonRegions().size() - 1;
		return new SmallVariantAnnotationLocation(transcript, rankType, rank, totalRank, txLocation);
	}

	/** @return affected {@link TranscriptModel} */
	public TranscriptModel getTranscript() {
		return transcript;
	}

	/** Set affected {@link TranscriptModel} */
	public void setTranscript(TranscriptModel transcript) {
		this.transcript = transcript;
	}

	/** @return current {@link RankType} */
	public RankType getRankType() {
		return rankType;
	}

	/** Set current {@link RankType} */
	public void setRankType(RankType rankType) {
		this.rankType = rankType;
	}

	/** @return current rank */
	public int getRank() {
		return rank;
	}

	/** Set current rank */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/** @return current location on the transcript level */
	public TranscriptInterval getTXLocation() {
		return txLocation;
	}

	/** Set location on the transcript level */
	public void setTXLocation(TranscriptInterval txLocation) {
		this.txLocation = txLocation;
	}

}
