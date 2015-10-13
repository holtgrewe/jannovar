package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.annotation.SmallVariantAnnotation;
import de.charite.compbio.jannovar.annotation.SmallVariantAnnotationLocation;
import de.charite.compbio.jannovar.annotation.SmallVariantAnnotationLocation.RankType;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.SmallGenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;

// TODO(holtgrem): Make AnnotationBuilder an interface and rename AnnotationBuilder to AnnotationBuilderBase?

/**
 * Class providing static functions for creating {@link SmallVariantAnnotation} objects for SVs.
 *
 * This is currently not inheriting from {@link AnnotationBuilder} since it uses non of its functionality.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class StructuralVariantAnnotationBuilder {

	/** the transcript to build the annotation for */
	private final TranscriptModel transcript;
	/** the genome change to build the annotation for */
	private final SmallGenomeVariant change;

	/**
	 * Initialize the builder for the structural variant {@link SmallGenomeVariant} in the given {@link TranscriptInfo}.
	 *
	 * @param transcript
	 *            {@link TranscriptInfo} for the transcript to compute the affection for, use <code>null</code> for
	 *            intergenic variants
	 * @param change
	 *            {@link SmallGenomeVariant} to compute the annotation for, must describe a structural variant affecting
	 *            <code>transcript</code>
	 */
	public StructuralVariantAnnotationBuilder(TranscriptModel transcript, SmallGenomeVariant change) {
		this.transcript = transcript;
		this.change = change;
	}

	/**
	 * Build annotation for {@link #transcript} and {@link #change}
	 *
	 * @return {@link SmallVariantAnnotation} for the given {@link #transcript} and {@link #change}.
	 */
	public SmallVariantAnnotation build() {
		// Obtain shortcuts.
		GenomePosition position = change.getGenomePos();
		final int beginPos = position.getPos();
		final String ref = change.getRef();
		final String alt = change.getAlt();

		// Build reverse-complement of alt string.
		StringBuilder altRC = new StringBuilder(alt).reverse();
		for (int i = 0; i < altRC.length(); ++i)
			if (altRC.charAt(i) == 'A')
				altRC.setCharAt(i, 'T');
			else if (altRC.charAt(i) == 'T')
				altRC.setCharAt(i, 'A');
			else if (altRC.charAt(i) == 'C')
				altRC.setCharAt(i, 'G');
			else if (altRC.charAt(i) == 'G')
				altRC.setCharAt(i, 'C');

		// chromosome/genome level change
		NucleotideChange ntChange = new GenomicNucleotideChangeBuilder(change).build();

		// TODO(holtgrem): we should care about breakpoints within genes

		final SmallVariantAnnotationLocation annoLoc = new SmallVariantAnnotationLocation(null, RankType.UNDEFINED,
				SmallVariantAnnotationLocation.INVALID_RANK, SmallVariantAnnotationLocation.INVALID_RANK, null);

		if (ref.length() == alt.length() && ref.equals(altRC.toString())) { // SV inversion
			if (transcript == null) {
				return new SmallVariantAnnotation(null, change, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT,
						VariantEffect.STRUCTURAL_VARIANT), null, ntChange, null, null);
			} else {
				return new SmallVariantAnnotation(transcript, change, ImmutableList.of(VariantEffect.STRUCTURAL_VARIANT), annoLoc,
						ntChange, null, null);

			}
		} else if (ref.length() == 0) { // SV insertion
			// if transcript is null it is intergenic
			if (transcript == null) {
				return new SmallVariantAnnotation(null, change, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT,
						VariantEffect.STRUCTURAL_VARIANT), null, ntChange, null, null);
			} else {
				return new SmallVariantAnnotation(transcript, change, ImmutableList.of(VariantEffect.STRUCTURAL_VARIANT), annoLoc,
						ntChange, null, null);
			}
		} else if (alt.length() == 0) { // SV deletion
			// if tm is null it is intergenic
			if (transcript == null) {
				return new SmallVariantAnnotation(null, change, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT,
						VariantEffect.STRUCTURAL_VARIANT), null, ntChange, null, null);
			} else {
				return new SmallVariantAnnotation(this.transcript, change, ImmutableList.of(VariantEffect.STRUCTURAL_VARIANT),
						annoLoc, ntChange, null, null);
			}
		} else { // SV substitution
			if (transcript == null) {
				return new SmallVariantAnnotation(null, change, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT,
						VariantEffect.STRUCTURAL_VARIANT), null, ntChange, null, null);
			} else {
				return new SmallVariantAnnotation(transcript, change, ImmutableList.of(VariantEffect.STRUCTURAL_VARIANT), annoLoc,
						ntChange, null, null);
			}
		}
	}
}
