package de.charite.compbio.jannovar.reference;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.VariantAnnotation;
import de.charite.compbio.jannovar.impl.util.DNAUtils;

// TODO(holtgrewe): We only want genome changes on the forward strand, make sure this does not lead to problems downstream.

/**
 * Denote a "small" change with a "REF" and an "ALT" string using genome coordinates.
 * 
 * "Small" changes are in contrast to structural variants and are described by substitution of REF by ALT.
 *
 * GenomeChange objects are immutable, the members are automatically adjusted for the longest common suffix and prefix
 * in REF and ALT.
 *
 * Symbolic alleles, as in the VCF standard, are also possible, but methods like {@link #getType} etc. do not return
 * sensible results.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
@Immutable
public final class SmallGenomeVariant extends GenomeVariant {

	/**
	 * Construct object given the position, reference, and alternative nucleic acid string.
	 */
	public SmallGenomeVariant(GenomePosition pos, String ref, String alt) {
		super(pos, ref, alt);
		// TODO(holtgrew): throw in case of symbolic variant
	}

	/**
	 * Construct object given the position, reference, alternative nucleic acid string, and strand.
	 *
	 * On construction, pos, ref, and alt are automatically adjusted to the right/incremented by the length of the
	 * longest common prefix and suffix of ref and alt. Further, the position is adjusted to the given strand.
	 */
	public SmallGenomeVariant(GenomePosition pos, String ref, String alt, Strand strand) {
		super(pos, ref, alt, strand);
	}

	/**
	 * Construct object and enforce strand.
	 */
	public SmallGenomeVariant(SmallGenomeVariant other, Strand strand) {
		super(other, strand);
	}

	@Override
	public String getChrName() {
		return this.pos.getRefDict().getContigIDToName().get(this.pos.getChr());
	}

	public GenomePosition getGenomePos() {
		return this.pos;
	}

	@Override
	public int getPos() {
		return this.pos.getPos();
	}

	@Override
	public String getRef() {
		return this.ref;
	}

	@Override
	public String getAlt() {
		return this.alt;
	}

	/**
	 * @return numeric ID of chromosome this change is on
	 */
	@Override
	public int getChr() {
		return pos.getChr();
	}

	@Override
	public GenomeInterval getGenomeInterval() {
		if (isSymbolic())
			return new GenomeInterval(pos, 1);

		return new GenomeInterval(pos, ref.length());
	}

	@Override
	public SmallGenomeVariant withStrand(Strand strand) {
		return new SmallGenomeVariant(this, strand);
	}

	/**
	 * @return human-readable {@link String} describing the genome change
	 */
	@Override
	public String toString() {
		if (pos.getStrand() != Strand.FWD)
			return withStrand(Strand.FWD).toString();
		else if (ref.equals("")) // handle insertion as special case
			return Joiner.on("").join(getChrName(), ":g.", getPos(), "_", getPos() + 1, "ins", alt);
		else if (alt.equals("") && ref.length() == 1) // single-base deletion
			return Joiner.on("").join(getChrName(), ":g.", getPos() + 1, "del", ref);
		else if (alt.equals("") && ref.length() > 1) // multi-base deletion
			return Joiner.on("").join(getChrName(), ":g.", getPos() + 1, "_", getPos() + ref.length(), "del", ref);
		else if (ref.length() == 1 && alt.length() > 1)
			return Joiner.on("").join(getChrName(), ":g.", getPos() + 1, "del", ref, "ins", alt);
		else if (ref.length() > 1 && alt.length() != 0)
			return Joiner.on("").join(getChrName(), ":g.", getPos() + 1, "_", getPos() + ref.length(), "del", ref,
					"ins", alt);
		else
			return Joiner.on("").join(pos, (ref.equals("") ? "-" : ref), ">", (alt.equals("") ? "-" : alt));
	}

	/**
	 * @return the {@link SmallGenomeVariantType} of this GenomeChange
	 */
	public SmallGenomeVariantType getType() {
		if (ref.length() > 0 && alt.length() == 0)
			return SmallGenomeVariantType.DELETION;
		else if (ref.length() == 0 && alt.length() > 0)
			return SmallGenomeVariantType.INSERTION;
		else if (ref.length() == 1 && alt.length() == 1)
			return SmallGenomeVariantType.SNV;
		else
			return SmallGenomeVariantType.BLOCK_SUBSTITUTION;
	}

	/**
	 * A transition is purine <-> purine or pyrimidine <-> pyrimidine. Only applies to single nucleotide subsitutions.
	 *
	 * @return true if the variant is a SNV and a transition.
	 */
	public boolean isTransition() {
		if (getType() != SmallGenomeVariantType.SNV)
			return false;
		// purine to purine change
		if (this.ref.equals("A") && this.alt.equals("G"))
			return true;
		else if (this.ref.equals("G") && this.alt.equals("A"))
			return true;
		// pyrimidine to pyrimidine change
		if (this.ref.equals("C") && this.alt.equals("T"))
			return true;
		else if (this.ref.equals("T") && this.alt.equals("C"))
			return true;
		// If we get here, the variant must be a transversion.
		return false;
	}

	/**
	 * A transversion is purine <-> pyrimidine. Only applies to single nucleotide subsitutions.
	 *
	 * @return true if the variant is a SNV and a transversion.
	 */
	public boolean isTransversion() {
		if (getType() != SmallGenomeVariantType.SNV)
			return false;
		// purine to purine change
		if (this.ref.equals("A") && this.alt.equals("G"))
			return false;
		else if (this.ref.equals("G") && this.alt.equals("A"))
			return false;
		// pyrimidine to pyrimidine change
		if (this.ref.equals("C") && this.alt.equals("T"))
			return false;
		else if (this.ref.equals("T") && this.alt.equals("C"))
			return false;
		// If we get here, the variant must be a SNV and a transversion.
		return true;
	}

	@Override
	public int compareTo(VariantAnnotation other) {
		return ComparisonChain.start().compare(pos, other.getPos()).compare(ref, other.getRef())
				.compare(alt, other.getAlt()).result();
	}

}
