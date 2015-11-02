package de.charite.compbio.jannovar.svs.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.svs.SVDeletion;

/**
 * Build annotations for {@link SVDeletion} objects.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class SVDeletionAnnotationBuilder extends StructuralVariantAnnotationBuilder {

	SVDeletionAnnotationBuilder(TranscriptModel transcript, SVDeletion variant, AnnotationBuilderOptions options) {
		super(transcript, variant, options);
	}

	@Override
	public StructuralVariantAnnotation build() {
		return new StructuralVariantAnnotation(transcript, variant, getEffects(), ImmutableSet.<AnnotationMessage> of());
	}

	/**
	 * @return the effects that {@link #variant} has on {@link #transcript}
	 */
	private Collection<VariantEffect> getEffects() {
		List<VariantEffect> effects = new ArrayList<>();

		GenomeInterval affected = variant.getAffectedIntervalOuter().withStrand(Strand.FWD);

		// Case of transcript or exon loss/truncation
		if (variant.getAffectedIntervalOuter().contains(transcript.getTXRegion())) {
			effects.add(VariantEffect.TRANSCRIPT_ABLATION);
			effects.add(VariantEffect.COPY_NUMBER_LOSS);
		}
		if (effects.isEmpty()) {
			for (GenomeInterval exon : transcript.getExonRegions())
				if (affected.contains(exon)) {
					effects.add(VariantEffect.EXON_LOSS_VARIANT);
					effects.add(VariantEffect.COPY_NUMBER_LOSS);
				} else if (affected.overlapsWith(exon)) {
					effects.add(VariantEffect.FEATURE_TRUNCATION);
					effects.add(VariantEffect.COPY_NUMBER_LOSS);
				}
		}

		// Case of upstream/downstream/intergenic
		if (effects.isEmpty() && so.overlapsWithDownstreamRegion(affected))
			effects.add(VariantEffect.DOWNSTREAM_GENE_VARIANT);
		else if (effects.isEmpty() && so.overlapsWithUpstreamRegion(affected))
			effects.add(VariantEffect.UPSTREAM_GENE_VARIANT);
		else if (effects.isEmpty())
			effects.add(VariantEffect.INTERGENIC_VARIANT);

		// Variant is SV in any case and affects coding/non-coding tx
		effects.add(VariantEffect.STRUCTURAL_VARIANT);
		effects.add(VariantEffect.COPY_NUMBER_DECREASE);
		if (transcript.isCoding())
			effects.add(VariantEffect.CODING_TRANSCRIPT_VARIANT);
		else
			effects.add(VariantEffect.NON_CODING_TRANSCRIPT_VARIANT);

		return effects;
	}

}
