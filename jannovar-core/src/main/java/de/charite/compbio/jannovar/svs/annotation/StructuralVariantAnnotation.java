package de.charite.compbio.jannovar.svs.annotation;

import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.SmallVariantAnnotationLocation;
import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.PutativeImpact;
import de.charite.compbio.jannovar.annotation.VCFAnnotationData;
import de.charite.compbio.jannovar.annotation.VariantAnnotation;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinChange;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.SmallGenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.VariantDescription;
import de.charite.compbio.jannovar.svs.StructuralVariant;

// TODO(holtgrem): Test me!

/**
 * Collect the information for one {@link StructuralVariant}'s annotation.
 *
 * Implements the {@link VariantDescription} interface for quicker access to the variant description information.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class StructuralVariantAnnotation extends VariantAnnotation {

	public static StructuralVariantAnnotation buildIntergenicAnnotation(StructuralVariant variant) {
		return new StructuralVariantAnnotation(null, variant, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT),
				ImmutableSortedSet.<AnnotationMessage> of());
	}
	
	public StructuralVariantAnnotation(TranscriptModel transcript, StructuralVariant variant,
			Collection<VariantEffect> effects, Collection<AnnotationMessage> messages) {
		super(variant, effects, messages, transcript);
	}

	@Override
	public String toVCFAnnoString(String alt, boolean escape) {
		VCFAnnotationData data = new VCFAnnotationData();
		data.effects = effects;
		data.impact = getPutativeImpact();
		data.isCoding = transcript.isCoding();
		data.messages = messages;
		data.setTranscriptAndVariant(transcript, variant);
		if (escape)
			return data.toString(alt);
		else
			return data.toUnescapedString(alt);
	}

	@Override
	public String getSymbolAndAnnotation() {
		return Joiner.on(":").skipNulls().join(transcript.getGeneSymbol(), transcript.getAccession());
	}

	@Override
	public String toString() {
		return "StructuralVariantAnnotation [super=" + super.toString() + "]";
	}

}
