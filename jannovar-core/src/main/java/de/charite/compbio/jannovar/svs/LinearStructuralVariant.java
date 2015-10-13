package de.charite.compbio.jannovar.svs;

import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;

//TODO(holtgrewe): Test me!

/**
 * Linear structural variant Linear structural variant affect an interval on the genome (possibly with a confidence
 * interval around begin and end position). There <em>is</em> an end position (different to novel insertions) and the
 * variant is not complex and called nested with other variants.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public abstract class LinearStructuralVariant extends StructuralVariant {

	/** @return length of the structural variant */
	protected final int length;
	/** Lower bound (inclusive) of confidence interval around POSEND */
	protected final int ciPosEndLo;
	/** Upper bound (inclusive) of confidence interval around POSEND */
	protected final int ciPosEndHi;

	/** @return confidence interval around POS */
	public final GenomeInterval getCIEND() {
		return new GenomeInterval(pos.withStrand(Strand.FWD).shifted(length + ciPosEndLo), -ciPosEndLo + ciPosEndHi);
	}

	/**
	 * Initialize with the given values
	 * 
	 * @param pos
	 *            starting point of SV
	 * @param ref
	 *            reference allele, as in VCF
	 * @param alt
	 *            alternative allele, as in VCF
	 * @param ciPosLo
	 *            lower bound (inclusive) of confidence interval around POS
	 * @param ciPosHi
	 *            upper bound (inclusive) of confidence interval around POS
	 * @param length
	 *            length of the variant
	 * @param ciPosEndLo
	 *            lower bound (inclusive) of confidence interval around POSEND
	 * @param ciPosEndHi
	 *            upper bound (inclusive) of confidence interval around POSEND
	 * @param subType
	 *            sub type of the SV, if any
	 */
	protected LinearStructuralVariant(GenomePosition pos, String ref, String alt, int ciPosLo, int ciPosHi, int length,
			int ciPosEndLo, int ciPosEndHi, String subType) {
		super(pos, ref, alt, ciPosLo, ciPosHi, subType);
		this.length = length;
		this.ciPosEndLo = ciPosEndLo;
		this.ciPosEndHi = ciPosEndHi;
	}

	@Override
	public GenomePosition getGenomePosEnd() {
		return this.pos.shifted(length);
	}

	@Override
	public GenomeInterval getAffectedIntervalInner() {
		if (length >= length - ciPosHi - ciPosEndLo)
			return new GenomeInterval(pos.withStrand(Strand.FWD).shifted(ciPosHi), length - ciPosHi - ciPosEndLo);
		else
			return new GenomeInterval(pos, 0);
	}

	@Override
	public GenomeInterval getAffectedIntervalOuter() {
		return new GenomeInterval(pos.withStrand(Strand.FWD).shifted(-ciPosLo), ciPosLo + length + ciPosEndHi);
	}
	
	@Override
	public GenomeInterval getGenomeInterval() {
		return new GenomeInterval(pos.withStrand(Strand.FWD), length);
	}

	/** @return length of the linear structural variant */
	public int getLength() {
		return length;
	}

	/** @return inclusive lower bound on confidence interval around POSEND */
	public int getCIENDLo() {
		return ciPosEndLo;
	}

	/** @return inclusive upper bound on confidence interval around POSEND */
	public int getCIENDHi() {
		return ciPosEndHi;
	}

}
