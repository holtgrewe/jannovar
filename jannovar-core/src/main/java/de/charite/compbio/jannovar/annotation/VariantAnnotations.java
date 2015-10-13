package de.charite.compbio.jannovar.annotation;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMultiset;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.VariantDescription;

/**
 * A list of priority-sorted {@link SmallVariantAnnotation} objects.
 *
 * @see AllAnnotationListTextGenerator
 * @see BestAnnotationListTextGenerator
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class VariantAnnotations implements VariantDescription {

	/** the {@link GenomeVariant} that this <code>AnnotationList</code> contains entries for. */
	private final GenomeVariant variant;

	/** the list of the annotations */
	private final ImmutableList<VariantAnnotation> entries;

	/**
	 * @param change
	 *            to use for the empty list
	 * @return empty <code>AnnotationList</code> with the given {@link GenomeVariant}
	 */
	public static VariantAnnotations buildEmptyList(GenomeVariant change) {
		return new VariantAnnotations(change, ImmutableList.<VariantAnnotation> of());
	}

	/**
	 * Construct ImmutableAnnotationList from a {@link Collection} of {@link SmallVariantAnnotation} objects.
	 *
	 * Note that <code>variant</code> is converted to the forward strand using {@link GenomeVariant#withStrand}.
	 *
	 * @param variant
	 *            {@link GenomeVariant} that this anotation list annotates
	 * @param entries
	 *            {@link Collection} of {@link SmallVariantAnnotation} objects
	 */
	public VariantAnnotations(GenomeVariant variant, Collection<? extends VariantAnnotation> entries) {
		this.variant = variant.withStrand(Strand.FWD);
		this.entries = ImmutableList.copyOf(ImmutableSortedMultiset.copyOf(entries));
	}

	/**
	 * Return the {@link GenomeVariant} that this AnnotationList is annotated with.
	 *
	 * Note that the {@link GenomeVariant} is converted to be on the forward strand on construction of AnnotationList
	 * objects.
	 *
	 * @return {@link GenomeVariant} that this <code>AnnotationList</code> contains entries for.
	 */
	public GenomeVariant getGenomeVariant() {
		return variant;
	}

	/**
	 * @return the list of annotations
	 */
	public ImmutableList<VariantAnnotation> getAnnotations() {
		return entries;
	}

	/**
	 * @return <code>true</code> if the result of {@link #getAnnotations} is empty
	 */
	public boolean hasAnnotation() {
		return !entries.isEmpty();
	}

	/**
	 * @return {@link SmallVariantAnnotation} with highest predicted impact, or <code>null</code> if there is none.
	 */
	public VariantAnnotation getHighestImpactAnnotation() {
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
		final VariantAnnotation anno = getHighestImpactAnnotation();
		if (anno == null || anno.getEffects().isEmpty())
			return VariantEffect.SEQUENCE_VARIANT;
		else
			return anno.getEffects().first();
	}

	@Override
	public String toString() {
		return "AnnotationList(change=" + variant + ", entries=[" + entries + "])";
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
		VariantAnnotations other = (VariantAnnotations) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		return true;
	}

	public String getChrName() {
		return variant.getChrName();
	}

	public int getChr() {
		return variant.getChr();
	}

	public int getPos() {
		return variant.getPos();
	}

	public String getRef() {
		return variant.getRef();
	}

	public String getAlt() {
		return variant.getAlt();
	}

	public int compareTo(VariantAnnotation other) {
		return variant.compareTo(other);
	}

}
