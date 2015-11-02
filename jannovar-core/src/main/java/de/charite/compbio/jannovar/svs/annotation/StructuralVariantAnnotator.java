package de.charite.compbio.jannovar.svs.annotation;

import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.annotation.VariantAnnotation;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.svs.SVBreakEnd;
import de.charite.compbio.jannovar.svs.SVCopyNumberVariation;
import de.charite.compbio.jannovar.svs.SVDeletion;
import de.charite.compbio.jannovar.svs.SVDuplication;
import de.charite.compbio.jannovar.svs.SVInsertion;
import de.charite.compbio.jannovar.svs.SVInversion;
import de.charite.compbio.jannovar.svs.StructuralVariant;

/**
 * Obtain {@link Annotations} objects from
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class StructuralVariantAnnotator {

	/** configuration for annotation builders */
	final private AnnotationBuilderOptions options;
	/** the {@link TranscriptModel} use for annotation */
	final private TranscriptModel tx;
	/** the {@link StructuralVariant} use for annotation */
	final private StructuralVariant variant;

	/**
	 * Construct new VariantAnnotator, given a chromosome map.
	 *
	 * @param tx
	 *            {@link TranscriptModel}  to use for annotation
	 * @param variant
	 *            {@link StructuralVariant} to use for annotation
	 * @param options
	 *            configuration to use for building the annotations
	 */
	public StructuralVariantAnnotator(TranscriptModel tx, StructuralVariant variant,
			AnnotationBuilderOptions options) {
		this.tx = tx;
		this.variant = variant;
		this.options = options;
	}

	/**
	 * Build {@link StructuralVariantAnnotations} object from {@link StructuralVariant}
	 *
	 * @return {@link StructuralVariantAnnotation} for the {@link #variant} and {@link tx}
	 * @throws InvalidGenomeVariant
	 *             if there is a problem with {@link #variant}
	 */
	public VariantAnnotation build() throws InvalidGenomeVariant {
		switch (variant.getType()) {
		case DEL:
			return new SVDeletionAnnotationBuilder(tx, (SVDeletion) variant, options).build();
		case INS:
			return new SVInsertionAnnotationBuilder(tx, (SVInsertion)variant, options).build();
		case DUP:
			return new SVDuplicationAnnotationBuilder(tx, (SVDuplication) variant, options).build();
		case CNV:
			return new SVCopyNumberVariationAnnotationBuilder(tx, (SVCopyNumberVariation) variant, options).build();
		case INV:
			return new SVInversionAnnotationBuilder(tx, (SVInversion)variant, options).build();
		case BND:
			return new SVBreakEndAnnotationBuilder(tx, (SVBreakEnd)variant, options).build();
		default:
			throw new InvalidGenomeVariant("Invalid variant type " + variant.getType());
		}
	}

}
