package de.charite.compbio.jannovar.svs;

import de.charite.compbio.jannovar.reference.GenomePosition;

//TODO(holtgrewe): Test me!

/**
 * Class for representing a novel sequence insertion
 * 
 * Note that novel sequence insertions are considered linear variants with reference allele length 0.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class SVInsertion extends LinearStructuralVariant {

	/**
	 * Construct with the given parameters, see
	 * {@link LinearStructuralVariant#LinearStructuralVariant(GenomePosition, String, String, int, int, int, int, int, String)}
	 * for details.
	 */
	public SVInsertion(GenomePosition pos, String ref, String alt, int ciPosLo, int ciPosHi, int length,
			int ciPosEndLo, int ciPosEndHi, String subType) {
		super(pos, ref, alt, ciPosLo, ciPosHi, length, ciPosEndLo, ciPosEndHi, subType);
	}

	public SVType getType() {
		return SVType.INS;
	}

}
