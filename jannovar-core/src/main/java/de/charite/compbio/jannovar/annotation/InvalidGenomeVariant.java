package de.charite.compbio.jannovar.annotation;

import de.charite.compbio.jannovar.reference.SmallGenomeVariant;

/**
 * Thrown when the the given {@link SmallGenomeVariant} does not fit the used annotation builder class.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class InvalidGenomeVariant extends AnnotationException {

	private static final long serialVersionUID = -6983204936815945929L;

	public InvalidGenomeVariant() {
	}

	public InvalidGenomeVariant(String msg) {
		super(msg);
	}

	public InvalidGenomeVariant(String msg, Throwable t) {
		super(msg, t);
	}

}
