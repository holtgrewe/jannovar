package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.SmallVariantAnnotation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.annotation.VariantAnnotation;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.SmallGenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.svs.SVInversion;
import de.charite.compbio.jannovar.svs.StructuralVariant;
import de.charite.compbio.jannovar.svs.annotation.SVInversionAnnotationBuilder;
import de.charite.compbio.jannovar.svs.annotation.StructuralVariantAnnotation;
import de.charite.compbio.jannovar.svs.annotation.StructuralVariantAnnotator;

/**
 * Dispatches annotation building to the specific classes, depending on their {@link GenomeVariant#getType}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class AnnotationBuilderDispatcher {

	/** chromosome map to use */
	private final ImmutableMap<Integer, Chromosome> chromosomeMap;
	/** transcript to build annotation for */
	private final TranscriptModel transcript;
	/** {@link GenomeVariant} to build annotation for */
	private final GenomeVariant variant;
	/** configuration to use */
	private final AnnotationBuilderOptions options;

	public AnnotationBuilderDispatcher(ImmutableMap<Integer, Chromosome> chromosomeMap, TranscriptModel transcript,
			GenomeVariant change, AnnotationBuilderOptions options) {
		this.chromosomeMap = chromosomeMap;
		this.transcript = transcript;
		this.variant = change;
		this.options = options;
	}

	/**
	 * @return {@link VariantAnnotation} for {@link #transcript} and {@link #variant}
	 *
	 * @throws InvalidGenomeVariant
	 *             if there is a problem with {@link #variant}
	 */
	public VariantAnnotation build() throws InvalidGenomeVariant {
		if (variant instanceof SmallGenomeVariant)
			return buildForSmallVariant((SmallGenomeVariant) variant);
		else
			return null;
	}

	/** Build {@link VariantAnnotation} for {@link SmallGenomeVariant} */
	public VariantAnnotation buildForSmallVariant(SmallGenomeVariant variant) throws InvalidGenomeVariant {
		if (transcript == null)
			return new SmallVariantAnnotation(null, variant, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT), null,
					new GenomicNucleotideChangeBuilder(variant).build(), null, null);

		switch (variant.getType()) {
		case SNV:
			return new SNVAnnotationBuilder(transcript, variant, options).build();
		case DELETION:
			return new DeletionAnnotationBuilder(transcript, variant, options).build();
		case INSERTION:
			return new InsertionAnnotationBuilder(transcript, variant, options).build();
		case BLOCK_SUBSTITUTION:
		default:
			return new BlockSubstitutionAnnotationBuilder(transcript, variant, options).build();
		}
	}

	/** Build {@link VariantAnnotation} for {@link StructuralVariant} */
	public VariantAnnotation buildForStructuralVariant(StructuralVariant variant) throws InvalidGenomeVariant {
		if (transcript == null)
			return StructuralVariantAnnotation.buildIntergenicAnnotation(variant);
		else
			return new StructuralVariantAnnotator(transcript, variant, options).build();
	}
}
