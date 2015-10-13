package de.charite.compbio.jannovar.annotation.builders;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.annotation.SmallVariantAnnotation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.annotation.VariantAnnotation;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.SmallGenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Dispatches annotation building to the specific classes, depending on their {@link SmallGenomeVariant#getType}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class AnnotationBuilderDispatcher {

	/** transcript to build annotation for */
	private final TranscriptModel transcript;
	/** genomic change to build annotation for */
	private final SmallGenomeVariant change;
	/** configuration to use */
	private final AnnotationBuilderOptions options;

	public AnnotationBuilderDispatcher(TranscriptModel transcript, SmallGenomeVariant change,
			AnnotationBuilderOptions options) {
		this.transcript = transcript;
		this.change = change;
		this.options = options;
	}

	/**
	 * @return {@link SmallVariantAnnotation} for {@link #transcript} and {@link #change}
	 *
	 * @throws InvalidGenomeVariant
	 *             if there is a problem with {@link #change}
	 */
	public VariantAnnotation build() throws InvalidGenomeVariant {
		if (transcript == null)
			return new SmallVariantAnnotation(null, change, ImmutableList.of(VariantEffect.INTERGENIC_VARIANT), null,
					new GenomicNucleotideChangeBuilder(change).build(), null, null);

		switch (change.getType()) {
		case SNV:
			return new SNVAnnotationBuilder(transcript, change, options).build();
		case DELETION:
			return new DeletionAnnotationBuilder(transcript, change, options).build();
		case INSERTION:
			return new InsertionAnnotationBuilder(transcript, change, options).build();
		case BLOCK_SUBSTITUTION:
		default:
			return new BlockSubstitutionAnnotationBuilder(transcript, change, options).build();
		}
	}

}
