package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.annotation.SmallVariantAnnotation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.annotation.VariantAnnotation;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.SmallGenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Dispatches annotation building to the specific classes, depending on their {@link GenomeVariant#getType}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class AnnotationBuilderDispatcher {

	/** transcript to build annotation for */
	private final TranscriptModel transcript;
	/** {@link GenomeVariant} to build annotation for */
	private final GenomeVariant variant;
	/** configuration to use */
	private final AnnotationBuilderOptions options;

	public AnnotationBuilderDispatcher(TranscriptModel transcript, GenomeVariant change,
			AnnotationBuilderOptions options) {
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

}
