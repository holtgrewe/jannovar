package de.charite.compbio.jannovar.annotation;

import java.util.Collection;

import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.VariantDescription;

/**
 * Super class for {@link SmallVariantAnnotation} and {@link StructuralVariantAnnotation}
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public abstract class VariantAnnotation implements VariantDescription, Comparable<VariantAnnotation> {

	/**
	 * This line is added to the output of a VCF file annotated by Jannovar and describes the new field for the INFO
	 * section entitled EFFECT, which decribes the effects of variants (splicing,missense,stoploss, etc).
	 */
	public static final String INFO_EFFECT = ""
			+ "variant effect (UTR5,UTR3,intronic,splicing,missense,stoploss,stopgain,"
			+ "startloss,duplication,frameshift-insertion,frameshift-deletion,non-frameshift-deletion,"
			+ "non-frameshift-insertion,synonymous)";
	/**
	 * This line is added to the output of a VCF file annotated by Jannovar and describes the new field for the INFO
	 * section entitled HGVS, which provides the HGVS encoded variant corresponding to the chromosomal variant in the
	 * original VCF file.
	 */
	public static final String INFO_HGVS = "HGVS Nomenclature";
	/** The DESCRIPTION string to use in the VCF header for VCFVariantAnnotation objects */
	public static final String VCF_ANN_DESCRIPTION_STRING = "Functional annotations:'Allele|Annotation|"
			+ "Annotation_Impact|Gene_Name|Gene_ID|Feature_Type|Feature_ID|Transcript_BioType|Rank|HGVS.c|HGVS.p|"
			+ "cDNA.pos / cDNA.length|CDS.pos / CDS.length|AA.pos / AA.length|Distance|ERRORS / WARNINGS / INFO'";

	/** the annotated {@link GenomeVariant} */
	protected final GenomeVariant variant;
	/** variant types, sorted by internal pathogenicity score */
	protected final ImmutableSortedSet<VariantEffect> effects;
	/** errors and warnings */
	protected final ImmutableSortedSet<AnnotationMessage> messages;
	/** the transcript, <code>null</code> for {@link VariantEffect#INTERGENIC} annotations */
	protected final TranscriptModel transcript;

	protected VariantAnnotation(GenomeVariant variant, Collection<VariantEffect> effects,
			Collection<AnnotationMessage> messages, TranscriptModel transcript) {
		if (variant != null)
			variant = variant.withStrand(Strand.FWD); // enforce forward strand
		this.variant = variant;
		if (effects == null)
			this.effects = ImmutableSortedSet.<VariantEffect> of();
		else
			this.effects = ImmutableSortedSet.copyOf(effects);
		this.messages = ImmutableSortedSet.copyOf(messages);
		this.transcript = transcript;
	}

	/** @return the annotated {@link GenomeVariant} */
	public GenomeVariant getGenomeVariant() {
		return variant;
	}

	/** @return variant types, sorted by internal pathogenicity score */
	public ImmutableSortedSet<VariantEffect> getEffects() {
		return effects;
	}

	/** @return errors and warnings */
	public ImmutableSortedSet<AnnotationMessage> getMessages() {
		return messages;
	}

	/**
	 * Return the standardized VCF variant string for the given <code>ALT</code> allele.
	 *
	 * The <code>ALT</code> allele has to be given to this function since we trim away at least the first base of
	 * <code>REF</code>/<code>ALT</code>.
	 *
	 * @param escape
	 *            whether or not to escape the invalid VCF characters, e.g. <code>'='</code>.
	 */
	public abstract String toVCFAnnoString(String alt, boolean escape);

	/**
	 * Forward to {@link toVCFAnnoString}<code>(alt, true)</code>.
	 */
	public String toVCFAnnoString(String alt) {
		return toVCFAnnoString(alt, true);
	}

	/**
	 * Return the gene annotation or <code>"."</code> if it has no transcript.
	 *
	 * @return gene symbol or <code>"."</code>
	 */
	public String getGeneSymbol() {
		if (transcript == null || transcript.getGeneSymbol() == null)
			return ".";
		else
			return transcript.getGeneSymbol();
	}

	/**
	 * Return the full annotation with the gene symbol
	 */
	public abstract String getSymbolAndAnnotation();

	/**
	 * @return most pathogenic {@link VariantEffect} link {@link #effects}, <code>null</code> if none.
	 */
	public VariantEffect getMostPathogenicVarType() {
		if (effects.isEmpty())
			return null;
		return effects.first();
	}

	/** @return the transcript, <code>null</code> for {@link VariantEffect#INTERGENIC} annotations */
	public TranscriptModel getTranscript() {
		return transcript;
	}

	/**
	 * @return highest {@link PutativeImpact} of all {@link #effects}.
	 */
	public PutativeImpact getPutativeImpact() {
		if (effects.isEmpty())
			return null;
		VariantEffect worst = effects.first();
		for (VariantEffect vt : effects)
			if (worst.getImpact().compareTo(vt.getImpact()) > 0)
				worst = vt;
		return worst.getImpact();
	}

	@Override
	public String getChrName() {
		return variant.getChrName();
	}

	@Override
	public int getChr() {
		return variant.getChr();
	}

	@Override
	public int getPos() {
		return variant.getPos();
	}

	@Override
	public String getRef() {
		return variant.getRef();
	}

	@Override
	public String getAlt() {
		return variant.getAlt();
	}

	@Override
	public int compareTo(VariantAnnotation other) {
		if (getMostPathogenicVarType() == null && getMostPathogenicVarType() == other.getMostPathogenicVarType())
			return 0;
		else if (other.getMostPathogenicVarType() == null)
			return -1;
		else if (getMostPathogenicVarType() == null)
			return 1;

		int result = getMostPathogenicVarType().ordinal() - other.getMostPathogenicVarType().ordinal();
		if (result != 0)
			return result;

		if (transcript == null && other.transcript == null)
			return 0;
		else if (other.transcript == null)
			return -1;
		else if (transcript == null)
			return 1;

		return transcript.compareTo(other.transcript);
	}

}