package de.charite.compbio.jannovar.svs;

import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.Strand;

// TODO(holtgrewe): This needs more thinking and maybe better integration into the rest

/**
 * Representation of a break-end
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class SVBreakEnd extends StructuralVariant {

	/**
	 * Direction of breakend.
	 */
	public enum Direction {
		THREE_TO_THREE, THREE_TO_FIVE, FIVE_TO_THREE, FIVE_TO_FIVE;

		public static Direction fromDellyConnectionType(String ct) {
			switch (ct) {
			case "3to3":
				return THREE_TO_FIVE;
			case "3to5":
				return THREE_TO_FIVE;
			case "5to3":
				return FIVE_TO_THREE;
			case "5to5":
				return FIVE_TO_THREE;
			default:
				throw new RuntimeException("Unknown connection type " + ct);
			}
		}
	}

	/** End position of translocation */
	protected final GenomePosition posEnd;
	/** Lower bound (inclusive) of confidence interval around POSEND */
	protected final int ciPosEndLo;
	/** Upper bound (inclusive) of confidence interval around POSEND */
	protected final int ciPosEndHi;
	/** Direction of break-end */
	protected final Direction direction;

	public SVBreakEnd(GenomePosition pos, String ref, String alt, int ciPosLo, int ciPosHi, GenomePosition posEnd,
			int ciPosEndLo, int ciPosEndHi, Direction direction, String subType) {
		super(pos, ref, alt, ciPosLo, ciPosHi, subType);
		this.posEnd = posEnd;
		this.ciPosEndLo = ciPosEndLo;
		this.ciPosEndHi = ciPosEndHi;
		this.direction = direction;
	}
	
	@Override
	public SVType getType() {
		return SVType.BND;
	}

	@Override
	public GenomePosition getGenomePosEnd() {
		return posEnd;
	}

	@Override
	public GenomeInterval getAffectedIntervalInner() {
		return getAffectedIntervalOuter();
	}

	@Override
	public GenomeInterval getAffectedIntervalOuter() {
		return new GenomeInterval(pos.withStrand(Strand.FWD).shifted(ciPosLo), -ciPosLo + ciPosHi);
	}

	@Override
	public GenomeInterval getGenomeInterval() {
		return getAffectedIntervalOuter();
	}

	/**
	 * @return Confidence interval around end position.
	 */
	public GenomeInterval getAffectedIntervalPosEnd() {
		return new GenomeInterval(posEnd.withStrand(Strand.FWD).shifted(ciPosEndLo), -ciPosEndLo + ciPosEndHi);
	}

}
