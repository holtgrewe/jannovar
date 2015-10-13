package de.charite.compbio.jannovar.svs.annotation;

import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.annotation.AnnotationLocation;
import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.PutativeImpact;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinChange;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.SmallVariantDescription;
import de.charite.compbio.jannovar.svs.StructuralVariant;

// TODO(holtgrewe): Merge back with Annotation!
// TODO(holtgrem): Test me!

/**
 * Collect the information for one variant's annotation.
 *
 * Implements the {@link SmallVariantDescription} interface for quicker access to the variant description information.
 *
 * @see AnnotationVariantTypeDecorator
 * @see AnnotationTextGenerator
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class StructuralVariantAnnotation implements Comparable<StructuralVariantAnnotation> {

	/** The DESCRIPTION string to use in the VCF header for VCFVariantAnnotation objects */
	public static final String VCF_ANN_DESCRIPTION_STRING = "Functional annotations:'Allele|Annotation|"
			+ "Annotation_Impact|Gene_Name|Gene_ID|Feature_Type|Feature_ID|Transcript_BioType|Rank|HGVS.c|HGVS.p|"
			+ "cDNA.pos / cDNA.length|CDS.pos / CDS.length|AA.pos / AA.length|Distance|ERRORS / WARNINGS / INFO'";

	/** the annotated {@link StructuralVariant} */
	private final StructuralVariant variant;

	/** variant types, sorted by internal pathogenicity score */
	private final ImmutableSortedSet<VariantEffect> effects;

	/** errors and warnings */
	private final ImmutableSortedSet<AnnotationMessage> messages;

	/** location of the annotation, <code>null</code> if not even nearby a {@link TranscriptModel} */
	private final AnnotationLocation annoLoc;

	/** Chromosome/genome-level change, to be prepended with "g." */
	private final NucleotideChange genomicNTChange;

	/**
	 * CDS-level {@link NucleotideChange} for coding transcripts (to be prependend with "c.") and transcript level for
	 * non-coding transcripts (to be prepended with "n.")
	 */
	private final NucleotideChange cdsNTChange;

	/** change on the protein level */
	private final ProteinChange proteinChange;

	/** the transcript, <code>null</code> for {@link VariantEffect#INTERGENIC} annotations */
	private final TranscriptModel transcript;

	/**
	 * Initialize object with messages only.
	 *
	 * @param messages
	 *            {@link AnnotationMessage}s to use in this annotation
	 */
	public StructuralVariantAnnotation(Collection<AnnotationMessage> messages) {
		this(null, null, null, null, null, null, null, messages);
	}

	/**
	 * Initialize the {@link SmallVariantAnnotation} with the given values.
	 *
	 * The constructor will sort <code>effects</code> by pathogenicity before storing.
	 *
	 * @param transcript
	 *            transcript for this annotation
	 * @param variant
	 *            the annotated {@link StructuralVariant}
	 * @param effects
	 *            type of the variants
	 */
	public StructuralVariantAnnotation(TranscriptModel transcript, StructuralVariant variant,
			Collection<VariantEffect> effects) {
		this(transcript, variant, effects, null, null, null, null, ImmutableSortedSet.<AnnotationMessage> of());
	}

	/**
	 * Initialize the {@link SmallVariantAnnotation} with the given values.
	 *
	 * The constructor will sort <code>effects</code> by pathogenicity before storing.
	 *
	 * @param change
	 *            the annotated {@link StructuralVariant}
	 * @param transcript
	 *            transcript for this annotation
	 * @param effects
	 *            type of the variants
	 * @param annoLoc
	 *            location of the variant
	 * @param genomicNTChange
	 *            ghromosome/genome-level change, to be prepended with "g."
	 * @param cdsNTChange
	 *            CDS-level {@link NucleotideChange}
	 * @param proteinChange
	 *            {@link ProteinChange} with a predicted protein change
	 * @param messages
	 *            {@link Collection} of {@link AnnotatioMessage} objects
	 */
	public StructuralVariantAnnotation(TranscriptModel transcript, StructuralVariant change,
			Collection<VariantEffect> varTypes, AnnotationLocation annoLoc, NucleotideChange genomicNTChange,
			NucleotideChange cdsNTChange, ProteinChange proteinChange, Collection<AnnotationMessage> messages) {
		// TODO(holtgrewe): enforce forward strand
		this.variant = change;
		if (varTypes == null)
			this.effects = ImmutableSortedSet.<VariantEffect> of();
		else
			this.effects = ImmutableSortedSet.copyOf(varTypes);
		this.annoLoc = annoLoc;
		this.genomicNTChange = genomicNTChange;
		this.cdsNTChange = cdsNTChange;
		this.proteinChange = proteinChange;
		this.transcript = transcript;
		this.messages = ImmutableSortedSet.copyOf(messages);
	}

	/** @return the annotated {@link StructuralVariant} */
	public StructuralVariant getGenomeVariant() {
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

	/** @return location of the annotation, <code>null</code> if not even nearby a {@link TranscriptModel} */
	public AnnotationLocation getAnnoLoc() {
		return annoLoc;
	}

	/**
	 * @return {@link NucleotideChange} with genomic changes
	 */
	public NucleotideChange getGenomicNTChange() {
		return genomicNTChange;
	}

	/**
	 * @return genomic nucleotide change String, including the "g." prefix.
	 */
	public String getGenomicNTChangeStr() {
		return "g." + genomicNTChange.toHGVSString();
	}

	/**
	 * @return {@link NucleotideChange} with changes on the CDS level for coding transcripts and on the transcript level
	 *         otherwise, null if the change does not affect any transcript
	 */
	public NucleotideChange getCDSNTChange() {
		return cdsNTChange;
	}

	/** @return CDS nucleotide change String, including the "p." prefix or the empty string if there is no annotation. */
	public String getCDSNTChangeStr() {
		if (cdsNTChange == null || transcript == null)
			return "";
		else if (transcript.isCoding())
			return "c." + cdsNTChange.toHGVSString();
		else
			return "n." + cdsNTChange.toHGVSString();
	}

	/** @return predicted {@link ProteinChange} */
	public ProteinChange getProteinChange() {
		return proteinChange;
	}

	/** @return protein change String, including the "p." prefix or the empty string if there is no annotation. */
	public String getProteinChangeStr() {
		if (proteinChange == null)
			return "";
		else
			return "p." + proteinChange.toHGVSString();
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

	/**
	 * Return the standardized VCF variant string for the given <code>ALT</code> allele.
	 *
	 * The <code>ALT</code> allele has to be given to this function since we trim away at least the first base of
	 * <code>REF</code>/<code>ALT</code>.
	 *
	 * @param escape
	 *            whether or not to escape the invalid VCF characters, e.g. <code>'='</code>.
	 */
	public String toVCFAnnoString(String alt, boolean escape) {
		VCFAnnotationData data = new VCFAnnotationData();
		data.effects = effects;
		data.impact = getPutativeImpact();
		data.setTranscriptAndVariant(transcript, variant);
		data.setAnnoLoc(annoLoc);
		data.isCoding = transcript.isCoding();
		data.cdsNTChange = cdsNTChange;
		data.proteinChange = proteinChange;
		data.messages = messages;
		if (escape)
			return data.toString(alt);
		else
			return data.toUnescapedString(alt);
	}

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
	 * Return the full annotation with the gene symbol.
	 *
	 * If this annotation does not have a symbol (e.g., for an intergenic annotation) then just return the annotation
	 * string, e.g., <code>"KIAA1751:uc001aim.1:exon18:c.T2287C:p.X763Q"</code>.
	 *
	 * @return full annotation string or <code>null</code> if {@link #transcript} is <code>null</code>
	 */
	public String getSymbolAndAnnotation() {
		if (transcript == null)
			return null;
		return Joiner
				.on(":")
				.skipNulls()
				.join(transcript.getGeneSymbol(), transcript.getAccession(), getCDSNTChangeStr(), getProteinChangeStr());
	}

	/**
	 * @return most pathogenic {@link VariantEffect} link {@link #effects}, <code>null</code> if none.
	 */
	public VariantEffect getMostPathogenicVarType() {
		if (effects.isEmpty())
			return null;
		return effects.first();
	}

	@Override
	public int compareTo(StructuralVariantAnnotation other) {
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

	@Override
	public String toString() {
		if (proteinChange != null)
			return "Annotation [change=" + variant + ", effects=" + effects + ", cdsNTChange=" + cdsNTChange
					+ ", proteinChange=" + proteinChange.toHGVSString() + ", transcript.getAccession()="
					+ transcript.getAccession() + "]";
		else
			return "Annotation [change=" + variant + ", effects=" + effects + ", cdsNTChange=" + cdsNTChange
					+ ", proteinChange=" + proteinChange + ", transcript.getAccession()=" + transcript.getAccession()
					+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((proteinChange == null) ? 0 : proteinChange.hashCode());
		result = prime * result + ((annoLoc == null) ? 0 : annoLoc.hashCode());
		result = prime * result + ((effects == null) ? 0 : effects.hashCode());
		result = prime * result + ((messages == null) ? 0 : messages.hashCode());
		result = prime * result + ((cdsNTChange == null) ? 0 : cdsNTChange.hashCode());
		result = prime * result + ((transcript == null) ? 0 : transcript.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StructuralVariantAnnotation other = (StructuralVariantAnnotation) obj;
		if (proteinChange == null) {
			if (other.proteinChange != null)
				return false;
		} else if (!proteinChange.equals(other.proteinChange))
			return false;
		if (annoLoc == null) {
			if (other.annoLoc != null)
				return false;
		} else if (!annoLoc.equals(other.annoLoc))
			return false;
		if (effects == null) {
			if (other.effects != null)
				return false;
		} else if (!effects.equals(other.effects))
			return false;
		if (messages == null) {
			if (other.messages != null)
				return false;
		} else if (!messages.equals(other.messages))
			return false;
		if (cdsNTChange == null) {
			if (other.cdsNTChange != null)
				return false;
		} else if (!cdsNTChange.equals(other.cdsNTChange))
			return false;
		if (transcript == null) {
			if (other.transcript != null)
				return false;
		} else if (!transcript.equals(other.transcript))
			return false;
		return true;
	}

}
