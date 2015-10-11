package de.charite.compbio.jannovar.svs;

/**
 * Type of a structural variant
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public enum SVType {

	/** deletion */
	DEL,
	/** duplication */
	DUP,
	/** insertion */
	INS,
	/** copy number variable region */
	CNV,
	/** inversion */
	INV,
	/** other */
	OTHER;

}
