package de.charite.compbio.jannovar.newped;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.Pedigree;

/**
 * Given a list of variants for a gene, allows to query for compatible modes of inheritance.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ModeOfInheritanceChecker {

	final ImmutableList<VariantCall> callsForGene;
	final Pedigree pedigree;

	public ModeOfInheritanceChecker(ImmutableList<VariantCall> callsForGene, Pedigree pedigree) {
		this.callsForGene = callsForGene;
		this.pedigree = pedigree;
	}

	/**
	 * @return Compatible modes for each VariantCall.
	 */
	public ImmutableList<ImmutableSet<ModeOfInheritance>> compatibleModes() {
		ImmutableList.Builder<ImmutableSet<ModeOfInheritance>> builder = ImmutableList.builder();
		ImmutableList<Boolean> ad = isCompatibleAD();
		ImmutableList<Boolean> ar = isCompatibleAR();
		ImmutableList<Boolean> xd = isCompatibleXD();
		ImmutableList<Boolean> xr = isCompatibleXR();

		for (int i = 0; i < callsForGene.size(); ++i) {
			Set<ModeOfInheritance> modes = new HashSet<ModeOfInheritance>();
			if (ad.get(i))
				modes.add(ModeOfInheritance.AUTOSOMAL_DOMINANT);
			if (ar.get(i))
				modes.add(ModeOfInheritance.AUTOSOMAL_RECESSIVE);
			if (xd.get(i))
				modes.add(ModeOfInheritance.X_DOMINANT);
			if (xr.get(i))
				modes.add(ModeOfInheritance.X_RECESSIVE);
			builder.add(ImmutableSet.copyOf(modes));
		}

		return builder.build();
	}

	private ImmutableList<Boolean> isCompatibleAD() {
		return null;
	}

	private ImmutableList<Boolean> isCompatibleAR() {
		return null;
	}

	private ImmutableList<Boolean> isCompatibleXD() {
		return null;
	}

	private ImmutableList<Boolean> isCompatibleXR() {
		return null;
	}

}
