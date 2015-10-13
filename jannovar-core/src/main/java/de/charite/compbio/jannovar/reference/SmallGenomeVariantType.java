package de.charite.compbio.jannovar.reference;

/**
 * Types of genomic variants represented by {@link SmallGenomeVariant}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public enum SmallGenomeVariantType {
	/** single nucleotide variant */
	SNV,
	/** insertion */
	INSERTION,
	/** deletion */
	DELETION,
	/** block substitution */
	BLOCK_SUBSTITUTION,
	/** other, e.g., structural variant from symbolic allele */
	OTHER;
}
