package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.impl.util.DNAUtils;

/**
 * Super class for {@link SmallGenomeVariant} and {@link StructuralGenomeVariant}
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public abstract class GenomeVariant implements VariantDescription {

	/** position of the change */
	protected final GenomePosition pos;
	/** nucleic acid reference string */
	protected final String ref;
	/** nucleic acid alternative string */
	protected final String alt;

	/**
	 * Initialize with the given values, correct non-symbolic ref/alt alleles.
	 * 
	 * On construction, pos, ref, and alt are automatically adjusted to the right/incremented by the length of the
	 * longest common prefix and suffix of ref and alt.
	 * 
	 * An exception is if <code>ref</code> or <code>alt</code> encode a symbolic allele (start/end with <tt>'.'</tt>, or
	 * contain <tt>'['</tt>/<tt>']'</tt>/<tt>'&lt;'</tt>/<tt>'&gt;'</tt>. In this case, no adjustment is performed.
	 */
	protected GenomeVariant(GenomePosition pos, String ref, String alt) {
		if (!wouldBeSymbolicAllele(ref) && !wouldBeSymbolicAllele(alt)) {
			VariantDataCorrector corr = new VariantDataCorrector(ref, alt, pos.getPos());
			this.pos = new GenomePosition(pos.getRefDict(), pos.getStrand(), pos.getChr(), corr.position,
					PositionType.ZERO_BASED);
			this.ref = corr.ref;
			this.alt = corr.alt;
		} else {
			this.pos = pos;
			this.ref = ref;
			this.alt = alt;
		}
	}

	/**
	 * Construct object given the position, reference, alternative nucleic acid string, and strand.
	 *
	 * On construction, pos, ref, and alt are automatically adjusted to the right/incremented by the length of the
	 * longest common prefix and suffix of ref and alt. Further, the position is adjusted to the given strand.
	 */
	protected GenomeVariant(GenomePosition pos, String ref, String alt, Strand strand) {
		if (wouldBeSymbolicAllele(ref) || wouldBeSymbolicAllele(alt)) {
			this.pos = pos.withStrand(strand);
			if (strand == pos.getStrand()) {
				this.ref = ref;
				this.alt = alt;
			} else {
				this.ref = DNAUtils.reverseComplement(ref);
				this.alt = DNAUtils.reverseComplement(alt);
			}
			return;
		}

		// Correct variant data.
		VariantDataCorrector corr = new VariantDataCorrector(ref, alt, pos.getPos());
		if (strand == pos.getStrand()) {
			this.ref = corr.ref;
			this.alt = corr.alt;
		} else {
			this.ref = DNAUtils.reverseComplement(corr.ref);
			this.alt = DNAUtils.reverseComplement(corr.alt);
		}

		int delta = 0;
		if (strand != pos.getStrand() && ref.length() == 0)
			delta = -1;
		else if (strand != pos.getStrand() /* && ref.length() != 0 */)
			delta = ref.length() - 1;

		this.pos = new GenomePosition(pos.getRefDict(), pos.getStrand(), pos.getChr(), corr.position,
				PositionType.ZERO_BASED).shifted(delta).withStrand(strand);
	}

	/**
	 * Construct object and enforce strand.
	 */
	protected GenomeVariant(SmallGenomeVariant other, Strand strand) {
		if (strand == other.pos.getStrand()) {
			this.ref = other.ref;
			this.alt = other.alt;
		} else {
			this.ref = DNAUtils.reverseComplement(other.ref);
			this.alt = DNAUtils.reverseComplement(other.alt);
		}

		// Get position as 0-based position.

		if (strand == other.pos.getStrand()) {
			this.pos = other.pos;
		} else {
			this.pos = other.pos.shifted(this.ref.length() - 1).withStrand(strand);
		}
	}

	/**
	 * @return affected {@link GenomeInterval} of the variant
	 */
	public abstract GenomeInterval getGenomeInterval();

	/**
	 * @return the {@link GenomeVariant} on the given strand
	 */
	public abstract GenomeVariant withStrand(Strand strand);

	/**
	 * @return <code>true</code> if this is a symbolic allele, as described
	 */
	public boolean isSymbolic() {
		return (wouldBeSymbolicAllele(ref) || wouldBeSymbolicAllele(alt));
	}

	/**
	 * @return <code>true</code> if the given <code>allele</code> string describes a symbolic allele (events not
	 *         described by replacement of bases, e.g. break-ends or duplications that are described in one line).
	 */
	protected static boolean wouldBeSymbolicAllele(String allele) {
		if (allele.length() <= 1)
			return false;
		return (allele.charAt(0) == '<' || allele.charAt(allele.length() - 1) == '>') || // symbolic or large insertion
				(allele.charAt(0) == '.' || allele.charAt(allele.length() - 1) == '.') || // single breakend
				(allele.contains("[") || allele.contains("]")); // mated breakend
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alt == null) ? 0 : alt.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
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

		GenomeVariant other = (GenomeVariant) obj;
		if (other.pos.getStrand() != this.pos.getStrand())
			other = other.withStrand(this.pos.getStrand());

		if (alt == null) {
			if (other.alt != null)
				return false;
		} else if (!alt.equals(other.alt))
			return false;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		return true;
	}

}
