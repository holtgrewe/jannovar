package de.charite.compbio.jannovar.annotation;

import java.util.ArrayList;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderDispatcher;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.annotation.builders.StructuralVariantAnnotationBuilder;
import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.SmallGenomeVariant;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.svs.StructuralVariant;
import de.charite.compbio.jannovar.svs.annotation.StructuralVariantAnnotator;

// TODO(holtgrem): We should directly pass in a JannovarData object after adding the interval trees to it. Then, this should be fine.
// TODO(holtgrem): Merge SV and small variant class hierarchies, then merge with StructuralVariantAnnotator?

/**
 * Main driver class for annotating variants.
 *
 * Given, a chromosome map, objects of this class can be used to annotate variants identified by a genomic position
 * (chr, pos), a reference, and an alternative nucleotide String.
 *
 * @see StructuralVariantAnnotator
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Marten Jaeger <marten.jaeger@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public final class VariantAnnotator {

	/** configuration for annotation builders */
	final private AnnotationBuilderOptions options;

	/** {@link ReferenceDictionary} to use for genome information. */
	final private ReferenceDictionary refDict;

	/** {@link Chromosome}s with their {@link TranscriptInfo} objects. */
	final private ImmutableMap<Integer, Chromosome> chromosomeMap;

	/**
	 * This object will be used to prioritize the annotations and to choose the one(s) to report. For instance, if we
	 * have both an intronic and a nonsense mutation, just report the nonsense mutation. Note that the object will be
	 * initialized once in the constructor of the Chromosome class and will be reset for each new annotation, rather
	 * than creating a new object for each variation. Also note that the constructor takes an integer value with which
	 * the lists of potential annotations get initialized. We will take 2*SPAN because this is the maximum number of
	 * annotations any variant can get with this program.
	 */
	final private AnnotationCollector annovarFactory = new AnnotationCollector(20);

	/**
	 * Construct new VariantAnnotator, given a chromosome map.
	 *
	 * @param refDict
	 *            {@link ReferenceDictionary} with information about the genome.
	 * @param chromosomeMap
	 *            chromosome map to use for the annotator.
	 * @param options
	 *            configuration to use for building the annotations
	 */
	public VariantAnnotator(ReferenceDictionary refDict, ImmutableMap<Integer, Chromosome> chromosomeMap,
			AnnotationBuilderOptions options) {
		this.refDict = refDict;
		this.chromosomeMap = chromosomeMap;
		this.options = options;
	}

	// TODO(holtgrem): Remove this?
	/**
	 * Convenience function for obtaining an {@link VariantAnnotations} from genome change in primitive types.
	 *
	 * Forwards to {@link #buildSmallVariantAnnotations(int, int, String, String, PositionType)} and we recommend to use
	 * this function directly.
	 *
	 * @param position
	 *            The start position of the variant on this chromosome (one-based numbering)
	 * @param ref
	 *            String representation of the reference sequence affected by the variant
	 * @param alt
	 *            String representation of the variant (alt) sequence
	 * @param posType
	 *            the position type to use
	 * @return {@link VariantAnnotations} for the given genome change
	 * @throws AnnotationException
	 *             on problems building the annotation list
	 */
	public VariantAnnotations buildSmallVariantAnnotations(int chr, int position, String ref, String alt,
			PositionType posType) throws AnnotationException {
		// Get chromosome by id.
		if (chromosomeMap.get(chr) == null)
			throw new AnnotationException(String.format("Could not identify chromosome \"%d\"", chr));

		// Build the GenomeChange to build annotation for.
		GenomePosition pos = new GenomePosition(refDict, Strand.FWD, chr, position, posType);
		SmallGenomeVariant change = new SmallGenomeVariant(pos, ref, alt);

		return buildAnnotations(change);
	}

	/** Build annotation for small and structural {@link GenomeVariant}s */
	public VariantAnnotations buildAnnotations(GenomeVariant variant) throws AnnotationException {
		if (variant.isSymbolic())
			return buildStructuralVariantAnnotations((StructuralVariant) variant);
		else
			return buildSmallVariantAnnotations((SmallGenomeVariant) variant);
	}

	/**
	 * Main entry point to getting annotations for structural variants.
	 *
	 * When we get to this point, the client code has identified the right chromosome, and we are provided the
	 * coordinates on that chromosome.
	 *
	 * @param variant
	 *            the {@link StructuralVariant} to annotate
	 * @return {@link VariantAnnotations} for the {@link StructuralVariant}
	 * @throws AnnotationException
	 *             on problems building the annotation list
	 */
	private VariantAnnotations buildStructuralVariantAnnotations(StructuralVariant variant) throws AnnotationException {
		if (!variant.isSymbolic())
			throw new RuntimeException("Structural genome variants must be symbolic!");

		// Get genomic change interval and reset the factory.
		final GenomeInterval changeInterval = variant.getGenomeInterval();
		this.annovarFactory.clearAnnotationLists();

		// Get the TranscriptModel objects that overlap with changeInterval.
		final Chromosome chr = chromosomeMap.get(variant.getChr());
		IntervalArray<TranscriptModel>.QueryResult qr;
		if (changeInterval.length() == 0)
			qr = chr.getTMIntervalTree().findOverlappingWithPoint(changeInterval.getBeginPos());
		else
			qr = chr.getTMIntervalTree().findOverlappingWithInterval(changeInterval.getBeginPos(),
					changeInterval.getEndPos());
		ArrayList<TranscriptModel> candidateTranscripts = new ArrayList<TranscriptModel>(qr.getEntries());

		// If we reach here, then there is at least one transcript that overlaps with the query. Iterate over these
		// transcripts and collect annotations for each (they are collected in annovarFactory).
		try {
			for (TranscriptModel tm : candidateTranscripts)
				annovarFactory.addStructuralAnnotation(new StructuralVariantAnnotator(tm, variant, options).build());
		} catch (InvalidGenomeVariant e) {
			throw new AnnotationException("Problem annotating variant " + variant, e);
		}

		return annovarFactory.getAnnotationList(variant);
	}

	/**
	 * Main entry point to getting Annovar-type annotations for a small variant identified by chromosomal coordinates.
	 *
	 * When we get to this point, the client code has identified the right chromosome, and we are provided the
	 * coordinates on that chromosome.
	 *
	 * @param variant
	 *            the {@link SmallGenomeVariant} to annotate
	 * @return {@link VariantAnnotations} for the genome change
	 * @throws AnnotationException
	 *             on problems building the annotation list
	 */
	public VariantAnnotations buildSmallVariantAnnotations(SmallGenomeVariant variant) throws AnnotationException {
		if (variant.isSymbolic())
			throw new RuntimeException("Small genome variants must not be symbolic!");

		// Get genomic change interval and reset the factory.
		final GenomeInterval changeInterval = variant.getGenomeInterval();
		this.annovarFactory.clearAnnotationLists();

		// Get the TranscriptModel objects that overlap with changeInterval.
		final Chromosome chr = chromosomeMap.get(variant.getChr());
		IntervalArray<TranscriptModel>.QueryResult qr;
		if (changeInterval.length() == 0)
			qr = chr.getTMIntervalTree().findOverlappingWithPoint(changeInterval.getBeginPos());
		else
			qr = chr.getTMIntervalTree().findOverlappingWithInterval(changeInterval.getBeginPos(),
					changeInterval.getEndPos());
		ArrayList<TranscriptModel> candidateTranscripts = new ArrayList<TranscriptModel>(qr.getEntries());

		// Handle the case of no overlapping transcript. Then, create intergenic, upstream, or downstream annotations
		// and return the result.
		boolean isStructuralVariant = (variant.getRef().length() >= 1000 || variant.getAlt().length() >= 1000);
		if (candidateTranscripts.isEmpty()) {
			if (isStructuralVariant)
				buildSVAnnotation(variant, null);
			else
				buildNonSVAnnotation(variant, qr.getLeft(), qr.getRight());
			return annovarFactory.getAnnotationList(variant);
		}

		// If we reach here, then there is at least one transcript that overlaps with the query. Iterate over these
		// transcripts and collect annotations for each (they are collected in annovarFactory).
		for (TranscriptModel tm : candidateTranscripts)
			if (isStructuralVariant)
				buildSVAnnotation(variant, tm);
			else
				buildNonSVAnnotation(variant, tm);

		return annovarFactory.getAnnotationList(variant);
	}

	private void buildSVAnnotation(SmallGenomeVariant change, TranscriptModel transcript) throws AnnotationException {
		annovarFactory.addStructuralAnnotation(new StructuralVariantAnnotationBuilder(transcript, change).build());
	}

	private void buildNonSVAnnotation(SmallGenomeVariant change, TranscriptModel leftNeighbor,
			TranscriptModel rightNeighbor) throws AnnotationException {
		buildNonSVAnnotation(change, leftNeighbor);
		buildNonSVAnnotation(change, rightNeighbor);
	}

	private void buildNonSVAnnotation(SmallGenomeVariant change, TranscriptModel transcript)
			throws InvalidGenomeVariant {
		if (transcript != null) // TODO(holtgrew): Is not necessarily an exonic annotation!
			annovarFactory.addExonicAnnotation(new AnnotationBuilderDispatcher(chromosomeMap, transcript, change,
					options).build());
	}

}
