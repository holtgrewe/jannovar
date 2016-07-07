package de.charite.compbio.jannovar.htsjdkped;

import java.util.Collection;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.newped.VariantCall;
import htsjdk.variant.variantcontext.VariantContext;

public final class VariantContextToCall {
	
	final JannovarData jvData;

	public VariantContextToCall(JannovarData jvData) {
		super();
		this.jvData = jvData;
	}
	
	VariantCall buildOne(VariantContext ctx) {
		return null;
	}
	
	VariantCall buildMany(Collection<VariantContext> ctx) {
		return null;
	}

}
