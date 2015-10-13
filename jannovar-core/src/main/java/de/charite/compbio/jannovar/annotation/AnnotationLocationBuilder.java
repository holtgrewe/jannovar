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
public class AnnotationLocationBuilder {

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

	public TranscriptModel getTranscript() {
		return transcript;
	}

	public void setTranscript(TranscriptModel transcript) {
		this.transcript = transcript;
	}

	public RankType getRankType() {
		return rankType;
	}

	public void setRankType(RankType rankType) {
		this.rankType = rankType;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public TranscriptInterval getTXLocation() {
		return txLocation;
	}

	public void setTXLocation(TranscriptInterval txLocation) {
		this.txLocation = txLocation;
	}

}
