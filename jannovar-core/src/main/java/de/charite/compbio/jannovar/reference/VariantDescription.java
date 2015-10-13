package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.annotation.SmallVariantAnnotation;

/**
 * Minimal description of a variant as triple (position, ref, alt).
 * 
 * Note that this is used for both small and structural variants. In the case of small variants, common suffixes and
 * prefixes are removed in REF and ALT.
 *
 * The reference and alternative allele string are returned as trimmed, first stripping common suffixes then common
 * prefixes. Note that this is not the same as normalized variants (see the link below) but allows for easier querying
 * in programs.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @see {@link http://genome.sph.umich.edu/wiki/Variant_Normalization}
 */
public interface VariantDescription {

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
	 * @return String with the reference allele in the variant
	 */
	public String getRef();

	/**
	 * @return String with the alternative allele in the variant
	 */
	public String getAlt();

	/**
	 * @return <code>int</code> describing how <code>this</code> compares with <code>other</code>
	 */
	int compareTo(SmallVariantAnnotation other);

}
