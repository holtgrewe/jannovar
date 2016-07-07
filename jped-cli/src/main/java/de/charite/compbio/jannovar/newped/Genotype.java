package de.charite.compbio.jannovar.newped;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

/**
 * Represents a genotype as a list of value
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class Genotype {
	
	public static int NO_CALL = -1;
	public static int REF = 0;

	ImmutableList<Integer> lst;

	Genotype(Integer...values) {
		lst = ImmutableList.copyOf(values);
	}
	
	Genotype(Collection<Integer> values) {
		lst = ImmutableList.copyOf(values);
	}
	
	public Integer get(int index) {
		return lst.get(index);
	}

	public int size() {
		return lst.size();
	}
	
	/** Return a Genotype, one of 0/0, 0/1, 1/0, 1/1 */
	public Genotype decomposeIntoSimpleHets() {
		return null;
	}

	public boolean isRef() {
		for (Integer gt : lst)
			if (gt != REF)
				return false;
		return true;
	}

	public boolean hasNoCall() {
		return false;  // TODO: implement me
	}

	public boolean hasRef() {
		return false;  // TODO: implement me
	}
	
	public boolean hasAlt() {
		return false;  // TODO: implement me
	}

}
