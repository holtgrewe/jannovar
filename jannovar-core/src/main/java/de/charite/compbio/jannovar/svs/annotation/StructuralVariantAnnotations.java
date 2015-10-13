package de.charite.compbio.jannovar.svs.annotation;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMultiset;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.svs.StructuralVariant;

// TODO(holtgrewe): merge back with VariantAnnotations
// TODO(holtgrewe): test me!

/**
 * A list of priority-sorted {@link StructuralVariantAnnotation} objects.
 *
 * @see AllAnnotationListTextGenerator
 * @see BestAnnotationListTextGenerator
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class StructuralVariantAnnotations implements Comparable<StructuralVariantAnnotations> {

	/** the {@link StructuralVariant} that this <code>AnnotationList</code> contains entries for. */
	private final StructuralVariant variant;

	/** the list of the annotations */
	private final ImmutableList<StructuralVariantAnnotation> entries;

	/**
	 * @param change
	 *            to use for the empty list
	 * @return empty <code>AnnotationList</code> with the given {@link StructuralVariant}
	 */
	public static StructuralVariantAnnotations buildEmptyList(StructuralVariant change) {
		return new StructuralVariantAnnotations(change, ImmutableList.<StructuralVariantAnnotation> of());
	}

	/**
	 * Construct ImmutableAnnotationList from a {@link Collection} of {@link StructuralVariantAnnotation} objects.
	 *
	 * Note that <code>variant</code> is converted to the forward strand using {@link StructuralVariant#withStrand}.
	 *
	 * @param variant
	 *            {@link StructuralVariant} that this anotation list annotates
	 * @param entries
	 *            {@link Collection} of {@link StructuralVariantAnnotation} objects
	 */
	public StructuralVariantAnnotations(StructuralVariant variant, Collection<StructuralVariantAnnotation> entries) {
		this.variant = variant;
		this.entries = ImmutableList.copyOf(ImmutableSortedMultiset.copyOf(entries));
	}

	/**
	 * Return the {@link StructuralVariant} that this AnnotationList is annotated with.
	 *
	 * Note that the {@link StructuralVariant} is converted to be on the forward strand on construction of
	 * AnnotationList objects.
	 *
	 * @return {@link StructuralVariant} that this <code>AnnotationList</code> contains entries for.
	 */
	public StructuralVariant getStructuralVariant() {
		return variant;
	}

	/**
	 * @return the list of annotations
	 */
	public ImmutableList<StructuralVariantAnnotation> getAnnotations() {
		return entries;
	}

	/**
	 * @return <code>true</code> if the result of {@link #getAnnotations} is empty
	 */
	public boolean hasAnnotation() {
		return !entries.isEmpty();
	}

	/**
	 * @return {@link StructuralVariantAnnotation} with highest predicted impact, or <code>null</code> if there is none.
	 */
	public StructuralVariantAnnotation getHighestImpactAnnotation() {
		if (!hasAnnotation())
			return null;
		else
			return entries.get(0);
	}

	/**
	 * Convenience method.
	 *
	 * @return {@link VariantEffect} with the highest impact of all in {@link #entries} or
	 *         {@link VariantEffect.SEQUENCE_VARIANT} if {@link #entries} is empty or has no annotated effects.
	 */
	public VariantEffect getHighestImpactEffect() {
		final StructuralVariantAnnotation anno = getHighestImpactAnnotation();
		if (anno == null || anno.getEffects().isEmpty())
			return VariantEffect.SEQUENCE_VARIANT;
		else
			return anno.getEffects().first();
	}

	@Override
	public String toString() {
		return "AnnotationList(variant=" + variant + ", entries=[" + entries + "])";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StructuralVariantAnnotations other = (StructuralVariantAnnotations) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		return true;
	}

	public String getChrName() {
		return variant.getGenomePos().getRefDict().getContigIDToName().get(variant.getGenomePos().getChr());
	}

	public int getChr() {
		return variant.getChr();
	}

	public int getPos() {
		return variant.getGenomePos().withStrand(Strand.FWD).getPos();
	}

	public String getRef() {
		return variant.getRef();
	}

	public String getAlt() {
		return variant.getAlt();
	}

	@Override
	public int compareTo(StructuralVariantAnnotations other) {
		return variant.compareTo(other.getStructuralVariant());
	}

}
