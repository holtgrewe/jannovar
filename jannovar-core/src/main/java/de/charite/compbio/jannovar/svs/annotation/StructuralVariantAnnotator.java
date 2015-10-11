package de.charite.compbio.jannovar.svs.annotation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.annotation.AnnotationException;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.TranscriptModel;
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

	/** {@link ReferenceDictionary} to use for genome information. */
	final private ReferenceDictionary refDict;

	/** {@link Chromosome}s with their {@link TranscriptInfo} objects. */
	final private ImmutableMap<Integer, Chromosome> chromosomeMap;

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
	public StructuralVariantAnnotator(ReferenceDictionary refDict, ImmutableMap<Integer, Chromosome> chromosomeMap,
			AnnotationBuilderOptions options) {
		this.refDict = refDict;
		this.chromosomeMap = chromosomeMap;
		this.options = options;
	}

	/**
	 * Build {@link StructuralVariantAnnotations} object from {@link StructuralVariant}
	 *
	 * @param variant
	 *            the {@link StructuralVariant} to annotate
	 * @return {@link StructuralVariantAnnotations} for the <tt>variant</tt>
	 * @throws AnnotationException
	 *             on problems building the annotation list
	 */
	public StructuralVariantAnnotations buildAnnotations(StructuralVariant variant) throws AnnotationException {
		if (chromosomeMap.get(variant.getChr()) == null)
			throw new AnnotationException(String.format("Could not identify chromosome \"%d\"", variant.getChr()));

		switch (variant.getType()) {
		case DEL:
			return buildSVDeletionAnnotations(variant);
		case INS:
			return buildSVInsertionAnnotations(variant);
		case DUP:
			return buildSVDuplicationAnnotations(variant);
		case CNV:
			return buildSVCNVAnnotations(variant);
		case INV:
			return buildSVInversionAnnotations(variant);
		default:
			throw new AnnotationException("Invalid variant type " + variant.getType());
		}
	}

	private StructuralVariantAnnotations buildSVDeletionAnnotations(StructuralVariant variant) {
		List<StructuralVariantAnnotation> annos = new ArrayList<>();
		for (TranscriptModel tx : this.getOverlappingAndNeighbouringTranscripts(variant.getAffectedIntervalOuter()))
			annos.add(new SVDeletionAnnotationBuilder(tx, variant, options).build());
		return new StructuralVariantAnnotations(variant, annos);
	}

	private StructuralVariantAnnotations buildSVInsertionAnnotations(StructuralVariant variant) {
		List<StructuralVariantAnnotation> annos = new ArrayList<>();
		for (TranscriptModel tx : this.getOverlappingAndNeighbouringTranscripts(variant.getAffectedIntervalOuter()))
			annos.add(new SVInsertionAnnotationBuilder(tx, variant, options).build());
		return new StructuralVariantAnnotations(variant, annos);
	}

	private StructuralVariantAnnotations buildSVDuplicationAnnotations(StructuralVariant variant) {
		List<StructuralVariantAnnotation> annos = new ArrayList<>();
		for (TranscriptModel tx : this.getOverlappingAndNeighbouringTranscripts(variant.getAffectedIntervalOuter()))
			annos.add(new SVDuplicationAnnotationBuilder(tx, variant, options).build());
		return new StructuralVariantAnnotations(variant, annos);
	}

	private StructuralVariantAnnotations buildSVCNVAnnotations(StructuralVariant variant) {
		List<StructuralVariantAnnotation> annos = new ArrayList<>();
		for (TranscriptModel tx : this.getOverlappingAndNeighbouringTranscripts(variant.getAffectedIntervalOuter()))
			annos.add(new SVCopyNumberVariationAnnotationBuilder(tx, variant, options).build());
		return new StructuralVariantAnnotations(variant, annos);
	}

	private StructuralVariantAnnotations buildSVInversionAnnotations(StructuralVariant variant) {
		SVInversion inversion = (SVInversion)variant;
		Set<StructuralVariantAnnotation> annos = new HashSet<>();
		for (TranscriptModel tx : this.getOverlappingAndNeighbouringTranscripts(inversion.getCI()))
			annos.add(new SVCopyNumberVariationAnnotationBuilder(tx, inversion, options).build());
		for (TranscriptModel tx : this.getOverlappingAndNeighbouringTranscripts(inversion.getCIEND()))
			annos.add(new SVCopyNumberVariationAnnotationBuilder(tx, inversion, options).build());
		return new StructuralVariantAnnotations(inversion, annos);
	}

	private List<TranscriptModel> getOverlappingAndNeighbouringTranscripts(GenomeInterval affectedRegion) {
		final Chromosome chr = chromosomeMap.get(affectedRegion);
		IntervalArray<TranscriptModel>.QueryResult qr;
		if (affectedRegion.length() == 0)
			qr = chr.getTMIntervalTree().findOverlappingWithPoint(affectedRegion.getBeginPos());
		else
			qr = chr.getTMIntervalTree().findOverlappingWithInterval(affectedRegion.getBeginPos(),
					affectedRegion.getEndPos());

		ArrayList<TranscriptModel> candidateTranscripts = new ArrayList<TranscriptModel>();
		if (qr.getLeft() != null)
			candidateTranscripts.add(qr.getLeft());
		candidateTranscripts.addAll(qr.getEntries());
		if (qr.getRight() != null)
			candidateTranscripts.add(qr.getRight());
		return candidateTranscripts;
	}

}
