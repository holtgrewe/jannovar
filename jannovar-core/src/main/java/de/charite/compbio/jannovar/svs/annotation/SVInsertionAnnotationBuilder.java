package de.charite.compbio.jannovar.svs.annotation;

import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.svs.StructuralVariant;

/**
 * Build annotations for {@link SVInsertion} objects.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class SVInsertionAnnotationBuilder extends StructuralVariantAnnotationBuilder {

	SVInsertionAnnotationBuilder(TranscriptModel transcript, StructuralVariant variant, AnnotationBuilderOptions options) {
		super(transcript, variant, options);
	}

	@Override
	public StructuralVariantAnnotation build() {
		// TODO Auto-generated method stub
		return null;
	}

}
