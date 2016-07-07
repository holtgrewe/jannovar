package de.charite.compbio.jannovar.htsjdkped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.newped.ModeOfInheritanceChecker;
import de.charite.compbio.jannovar.newped.VariantCall;
import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Allows the annotation of VariantContext objects with modes of inheritance.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class AnnotateVariantContextModeOfInheritance {

	final JannovarData jvData;
	final Pedigree ped;
	final VariantContextToCall converter;

	public AnnotateVariantContextModeOfInheritance(JannovarData jvData, Pedigree ped) {
		super();
		this.jvData = jvData;
		this.ped = ped;
		converter = new VariantContextToCall(jvData);
	}

	public void annotateOne(VariantContext vc) {
		ImmutableList<VariantCall> xs = ImmutableList.of(converter.buildOne(vc));
		ImmutableList<ImmutableSet<ModeOfInheritance>> compatibleModes = new ModeOfInheritanceChecker(xs, ped)
				.compatibleModes();
		ArrayList<String> modes = new ArrayList<>();
		modes.add(Joiner.on(",").join(compatibleModes.get(0)));
		vc.getCommonInfo().putAttribute("INHERITANCE_MODES", modes);
	}

	public void annotateMany(List<VariantContext> vcs) {
		ImmutableList<VariantCall> xs = ImmutableList.of(converter.buildMany(vcs));
		ImmutableList<ImmutableSet<ModeOfInheritance>> compatibleModes = new ModeOfInheritanceChecker(xs, ped)
				.compatibleModes();
		for (int i = 0; i < vcs.size(); ++i) {
			ArrayList<String> modes = new ArrayList<>();
			modes.add(Joiner.on(",").join(compatibleModes.get(i)));
			vcs.get(i).getCommonInfo().putAttribute("INHERITANCE_MODES", modes);
		}
	}

}
