package de.charite.compbio.jannovar.svs.annotation;

import java.util.SortedSet;
import java.util.TreeSet;

import de.charite.compbio.jannovar.annotation.SmallVariantAnnotation;
import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.reference.SmallGenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptSequenceOntologyDecorator;
import de.charite.compbio.jannovar.svs.StructuralVariant;

/**
 * Perform annotation of {@link StructuralVariant} objects
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public abstract class StructuralVariantAnnotationBuilder {

	/** configuration */
	protected final AnnotationBuilderOptions options;

	/** transcript to annotate. */
	protected final TranscriptModel transcript;
	/** {@link StructuralVariant} to use for annotation */
	protected final StructuralVariant variant;
	/** decorator for {@link #transcript} */
	protected final TranscriptSequenceOntologyDecorator so;

	/** warnings and messages occuring during annotation process */
	protected SortedSet<AnnotationMessage> messages = new TreeSet<AnnotationMessage>();

	/**
	 * Initialize the helper object with the given <code>transcript</code> and <code>change</code>.
	 *
	 * Note that <tt>transcript</tt> can be on the reverse strand and this has to be dealt with accordingly.
	 *
	 * @param transcript
	 *            the {@link TranscriptInfo} to build the annotation for
	 * @param variant
	 *            the {@link SmallGenomeVariant} to use for building the annotation
	 * @param options
	 *            the configuration to use for the {@link AnnotationBuilder}
	 */
	StructuralVariantAnnotationBuilder(TranscriptModel transcript, StructuralVariant variant, AnnotationBuilderOptions options) {
		this.options = options;
		this.variant = variant;

		this.transcript = transcript;
		this.so = new TranscriptSequenceOntologyDecorator(transcript);
	}

	/**
	 * Build annotation for {@link #transcript} and {@link #variant}
	 *
	 * @return {@link StructuralVariantAnnotation} for the given {@link #transcript} and {@link #variant}.
	 */
	public abstract StructuralVariantAnnotation build();

}
