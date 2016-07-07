package de.charite.compbio.jannovar.newped;

import java.awt.List;
import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.reference.GenomePosition;

public final class VariantCall {

	final GenomePosition pos;
	final ImmutableList<String> sampleNames;
	final ImmutableList<Genotype> genotypes;
	final ImmutableMap<String, Genotype> sampleToGenotype;

	public VariantCall(GenomePosition pos, Collection<String> sampleNames, Collection<Genotype> genotypes) {
		super();
		this.pos = pos;
		this.sampleNames = ImmutableList.copyOf(sampleNames);
		this.genotypes = ImmutableList.copyOf(genotypes);
		
		ImmutableMap.Builder<String, Genotype> builder = ImmutableMap.builder();
		for (int i = 0; i < this.sampleNames.size(); ++i)
			builder.put(this.sampleNames.get(i), this.genotypes.get(i));
		this.sampleToGenotype = builder.build();
	}
	
	public GenomePosition getPos() {
		return pos;
	}
	
	public Genotype getGenotype(String sampleName) {
		return null;
	}

	public ImmutableList<String> getSampleNames() {
		return sampleNames;
	}

	public ImmutableList<Genotype> getGenotypes() {
		return genotypes;
	}

	public ImmutableMap<String, Genotype> getSampleToGenotype() {
		return sampleToGenotype;
	}

}
