package de.charite.compbio.jannovar.svs.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.svs.SVBreakEnd;
import de.charite.compbio.jannovar.svs.SVInversion;

/**
 * Build annotations for {@link SVInversion} objects.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class SVBreakEndAnnotationBuilder extends StructuralVariantAnnotationBuilder {

	public SVBreakEndAnnotationBuilder(TranscriptModel transcript, SVBreakEnd variant, AnnotationBuilderOptions options) {
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
		SVBreakEnd bnd = (SVBreakEnd) variant;

		List<VariantEffect> effects = new ArrayList<>();

		ImmutableList<GenomeInterval> affectedItvs = ImmutableList.of(bnd.getAffectedIntervalOuter(),
				bnd.getAffectedIntervalPosEnd());

		for (GenomeInterval affected : affectedItvs) {
			if (so.overlapsWithExon(affected))
				effects.add(VariantEffect.EXON_VARIANT);
			else if (so.overlapsWithIntron(affected))
				effects.add(VariantEffect.INTRON_VARIANT);
			else if (so.overlapsWithDownstreamRegion(affected))
				effects.add(VariantEffect.DOWNSTREAM_GENE_VARIANT);
			else if (so.overlapsWithUpstreamRegion(affected))
				effects.add(VariantEffect.UPSTREAM_GENE_VARIANT);
			else
				effects.add(VariantEffect.INTERGENIC_VARIANT);
		}

		// Variant is SV in any case and affects coding/non-coding tx
		effects.add(VariantEffect.STRUCTURAL_VARIANT);
		if (transcript.isCoding())
			effects.add(VariantEffect.CODING_TRANSCRIPT_VARIANT);
		else
			effects.add(VariantEffect.NON_CODING_TRANSCRIPT_VARIANT);

		return effects;
	}

}
