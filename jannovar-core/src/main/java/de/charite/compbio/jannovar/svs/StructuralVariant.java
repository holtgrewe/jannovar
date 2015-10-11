package de.charite.compbio.jannovar.svs;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.Strand;

//TODO(holtgrewe): Test me!
//TODO(holtgrewe): add support for Delly translocations

/**
 * Base class for structural variants
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public abstract class StructuralVariant implements Comparable<StructuralVariant> {

	/** Subtype as in VCF, e.g. "ME:L1", or "TANDEM" */
	protected final String subType;
	/** Position of the starting point of the SV */
	protected final GenomePosition pos;
	/** String representation of reference allele, as in VCF */
	protected final String ref;
	/** String representation of alternative allele, as in VCF */
	protected final String alt;
	/** Lower bound (inclusive) of confidence interval around POS */
	protected final int ciPosLo;
	/** Upper bound (inclusive) of confidence interval around POS */
	protected final int ciPosHi;

	/**
	 * @return {@link SVType} with the type of the structural variant
	 */
	public abstract SVType getType();

	/** @return end position of the linear structural variant */
	public abstract GenomePosition getGenomePosEnd();

	/** @return inner affected interval, using confidence intervals */
	public abstract GenomeInterval getAffectedIntervalInner();

	/** @return inner affected interval, using confidence intervals */
	public abstract GenomeInterval getAffectedIntervalOuter();

	/** @return confidence interval around POS */
	public final GenomeInterval getCI() {
		return new GenomeInterval(pos.withStrand(Strand.FWD).shifted(ciPosLo), -ciPosLo + ciPosHi);
	}

	/**
	 * Initialize object
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
	 * @param subType
	 *            sub type of the SV, if any
	 */
	protected StructuralVariant(GenomePosition pos, String ref, String alt, int ciPosLo, int ciPosHi, String subType) {
		super();
		this.pos = pos;
		this.ref = ref;
		this.alt = alt;
		this.ciPosLo = ciPosLo;
		this.ciPosHi = ciPosHi;
		this.subType = subType;
	}

	/** @return numeric ID of SV chromosome begin position */
	public int getChr() {
		return getGenomePos().getChr();
	}

	/** @return numeric ID of SV chromosome end position */
	public int getChrEnd() {
		return getGenomePosEnd().getChr();
	}

	/** @return {@link GenomePosition} of SV starting point */
	public GenomePosition getGenomePos() {
		return pos;
	}

	/** @return String representation of reference allele */
	public String getRef() {
		return ref;
	}

	/** @return String representation of alternative allele */
	public String getAlt() {
		return alt;
	}

	/** @return inclusive lower bound of confidence interval round POS */
	public int getCIPOSLo() {
		return ciPosLo;
	}

	/** @return inclusive upper bound of confidence interval round POS */
	public int getCIPOSHi() {
		return ciPosHi;
	}

	/** @return String with sub type as in VCF, e.g. "ME:L1" or "TANDEM", or <code>""</code> */
	public String getSubType() {
		return subType;
	}

	@Override
	public int compareTo(StructuralVariant o) {
		return getGenomePos().compareTo(o.getGenomePos());
	}

}
