package de.charite.compbio.jannovar.annotation;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMultiset;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.reference.SmallGenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.SmallVariantDescription;

/**
 * A list of priority-sorted {@link SmallVariantAnnotation} objects.
 *
 * @see AllAnnotationListTextGenerator
 * @see BestAnnotationListTextGenerator
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class VariantAnnotations implements SmallVariantDescription {

	/** the {@link SmallGenomeVariant} that this <code>AnnotationList</code> contains entries for. */
	private final SmallGenomeVariant change;

	/** the list of the annotations */
	private final ImmutableList<SmallVariantAnnotation> entries;

	/**
	 * @param change
	 *            to use for the empty list
	 * @return empty <code>AnnotationList</code> with the given {@link SmallGenomeVariant}
	 */
	public static VariantAnnotations buildEmptyList(SmallGenomeVariant change) {
		return new VariantAnnotations(change, ImmutableList.<SmallVariantAnnotation> of());
	}

	/**
	 * Construct ImmutableAnnotationList from a {@link Collection} of {@link SmallVariantAnnotation} objects.
	 *
	 * Note that <code>variant</code> is converted to the forward strand using {@link SmallGenomeVariant#withStrand}.
	 *
	 * @param change
	 *            {@link SmallGenomeVariant} that this anotation list annotates
	 * @param entries
	 *            {@link Collection} of {@link SmallVariantAnnotation} objects
	 */
	public VariantAnnotations(SmallGenomeVariant variant, Collection<SmallVariantAnnotation> entries) {
		this.change = variant.withStrand(Strand.FWD);
		this.entries = ImmutableList.copyOf(ImmutableSortedMultiset.copyOf(entries));
	}

	/**
	 * Return the {@link SmallGenomeVariant} that this AnnotationList is annotated with.
	 *
	 * Note that the {@link SmallGenomeVariant} is converted to be on the forward strand on construction of AnnotationList
	 * objects.
	 *
	 * @return {@link SmallGenomeVariant} that this <code>AnnotationList</code> contains entries for.
	 */
	public SmallGenomeVariant getGenomeVariant() {
		return change;
	}

	/**
	 * @return the list of annotations
	 */
	public ImmutableList<SmallVariantAnnotation> getAnnotations() {
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
	public SmallVariantAnnotation getHighestImpactAnnotation() {
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
		final SmallVariantAnnotation anno = getHighestImpactAnnotation();
		if (anno == null || anno.getEffects().isEmpty())
			return VariantEffect.SEQUENCE_VARIANT;
		else
			return anno.getEffects().first();
	}

	@Override
	public String toString() {
		return "AnnotationList(change=" + change + ", entries=[" + entries + "])";
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
		return change.getChrName();
	}

	public int getChr() {
		return change.getChr();
	}

	public int getPos() {
		return change.getPos();
	}

	public String getRef() {
		return change.getRef();
	}

	public String getAlt() {
		return change.getAlt();
	}

	public int compareTo(SmallVariantAnnotation other) {
		return change.compareTo(other);
	}

}
