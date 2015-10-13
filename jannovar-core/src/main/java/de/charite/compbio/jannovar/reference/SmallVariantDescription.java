package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.annotation.Annotation;

/**
 * Minimal description of a small variant as triple (position, ref, alt).
 * 
 * "Small" variants are those that are be described by the substitution of a string. Note that there is no restriction
 * on the length of ref and alt. Instead, the name is meant to contrast {@link StructuralVariantDescription} where the
 * change is described in means of only positions and intervals in the genome.
 *
 * The reference and alternative allele string are returned as trimmed, first stripping common suffixes then common
 * prefixes. Note that this is not the same as normalized variants (see the link below) but allows for easier querying
 * in programs.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @see {@link http://genome.sph.umich.edu/wiki/Variant_Normalization}
 */
public interface SmallVariantDescription {

	/**
	 * @return String with the canonical chromosome name
	 */
	public String getChrName();

	/**
	 * @return integer identifying the chromosome
	 */
	public int getChr();

	/**
	 * @return zero-based position of the variant on the chromosome
	 */
	public int getPos();

	/**
	 * @return String with the reference allele in the variant, without common suffix or prefix to reference allele.
	 */
	public String getRef();

	/**
	 * @return String with the alternative allele in the variant, without common suffix or prefix to reference allele.
	 */
	public String getAlt();

	/**
	 * @return <code>int</code> describing how <code>this</code> compares with <code>other</code>.
	 */
	int compareTo(Annotation other);

}
