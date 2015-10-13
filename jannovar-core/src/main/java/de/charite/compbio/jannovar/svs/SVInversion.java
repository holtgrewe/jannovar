package de.charite.compbio.jannovar.svs;

import de.charite.compbio.jannovar.reference.GenomePosition;

// TODO(holtgrewe): Test me!

/**
 * Class for representing an inversion
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public final class SVInversion extends LinearStructuralVariant {

	/**
	 * Construct with the given parameters, see
	 * {@link LinearStructuralVariant#LinearStructuralVariant(GenomePosition, String, String, int, int, int, int, int, String)}
	 * for details.
	 */
	public SVInversion(GenomePosition pos, String ref, String alt, int ciPosLo, int ciPosHi, int length,
			int ciPosEndLo, int ciPosEndHi, String subType) {
		super(pos, ref, alt, ciPosLo, ciPosHi, length, ciPosEndLo, ciPosEndHi, subType);
	}

	public SVType getType() {
		return SVType.INV;
	}

}
