package de.charite.compbio.jannovar.annotation;

import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinChange;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.SmallGenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.VariantDescription;

// TODO(holtgrem): Test me!

/**
 * Collect the information for one variant's annotation.
 *
 * Implements the {@link VariantDescription} interface for quicker access to the variant description information.
 *
 * @see AnnotationVariantTypeDecorator
 * @see AnnotationTextGenerator
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class SmallVariantAnnotation extends VariantAnnotation {

	/** location of the annotation, <code>null</code> if not even nearby a {@link TranscriptModel} */
	final SmallVariantAnnotationLocation annoLoc;

	/** Chromosome/genome-level change, to be prepended with "g." */
	private final NucleotideChange genomicNTChange;

	/**
	 * CDS-level {@link NucleotideChange} for coding transcripts (to be prependend with "c.") and transcript level for
	 * non-coding transcripts (to be prepended with "n.")
	 */
	final NucleotideChange cdsNTChange;

	/** change on the protein level */
	final ProteinChange proteinChange;

	/**
	 * Initialize object with messages only.
	 *
	 * @param messages
	 *            {@link AnnotationMessage}s to use in this annotation
	 */
	public SmallVariantAnnotation(Collection<AnnotationMessage> messages) {
		this(null, null, null, null, null, null, null, messages);
	}

	/**
	 * Initialize the {@link SmallVariantAnnotation} with the given values.
	 *
	 * The constructor will sort <code>effects</code> by pathogenicity before storing.
	 *
	 * @param change
	 *            the annotated {@link SmallGenomeVariant}
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
	 *            predicted {@link ProteinChange}
	 */
	public SmallVariantAnnotation(TranscriptModel transcript, SmallGenomeVariant change,
			Collection<VariantEffect> varTypes, SmallVariantAnnotationLocation annoLoc,
			NucleotideChange genomicNTChange, NucleotideChange cdsNTChange, ProteinChange proteinChange) {
		this(transcript, change, varTypes, annoLoc, genomicNTChange, cdsNTChange, proteinChange, ImmutableSortedSet
				.<AnnotationMessage> of());
	}

	/**
	 * Initialize the {@link SmallVariantAnnotation} with the given values.
	 *
	 * The constructor will sort <code>effects</code> by pathogenicity before storing.
	 *
	 * @param variant
	 *            the annotated {@link GenomeVariant}
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
	public SmallVariantAnnotation(TranscriptModel transcript, GenomeVariant variant, Collection<VariantEffect> effects,
			SmallVariantAnnotationLocation annoLoc, NucleotideChange genomicNTChange, NucleotideChange cdsNTChange,
			ProteinChange proteinChange, Collection<AnnotationMessage> messages) {
		super(variant, effects, messages, transcript);
		this.annoLoc = annoLoc;
		this.genomicNTChange = genomicNTChange;
		this.cdsNTChange = cdsNTChange;
		this.proteinChange = proteinChange;
	}

	/** @return location of the annotation, <code>null</code> if not even nearby a {@link TranscriptModel} */
	public SmallVariantAnnotationLocation getAnnoLoc() {
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

	@Override
	public String toVCFAnnoString(String alt, boolean escape) {
		VCFAnnotationData data = new VCFAnnotationData();
		data.effects = effects;
		data.impact = getPutativeImpact();
		data.setTranscriptAndChange(transcript, variant);
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
	 * Return the full annotation with the gene symbol.
	 *
	 * If this annotation does not have a symbol (e.g., for an intergenic annotation) then just return the annotation
	 * string, e.g., <code>"KIAA1751:uc001aim.1:exon18:c.T2287C:p.X763Q"</code>.
	 *
	 * @return full annotation string or <code>null</code> if {@link #transcript} is <code>null</code>
	 */
	@Override
	public String getSymbolAndAnnotation() {
		if (transcript == null)
			return null;
		return Joiner
				.on(":")
				.skipNulls()
				.join(transcript.getGeneSymbol(), transcript.getAccession(), getCDSNTChangeStr(), getProteinChangeStr());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annoLoc == null) ? 0 : annoLoc.hashCode());
		result = prime * result + ((cdsNTChange == null) ? 0 : cdsNTChange.hashCode());
		result = prime * result + ((genomicNTChange == null) ? 0 : genomicNTChange.hashCode());
		result = prime * result + ((proteinChange == null) ? 0 : proteinChange.hashCode());
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
		SmallVariantAnnotation other = (SmallVariantAnnotation) obj;
		if (annoLoc == null) {
			if (other.annoLoc != null)
				return false;
		} else if (!annoLoc.equals(other.annoLoc))
			return false;
		if (cdsNTChange == null) {
			if (other.cdsNTChange != null)
				return false;
		} else if (!cdsNTChange.equals(other.cdsNTChange))
			return false;
		if (genomicNTChange == null) {
			if (other.genomicNTChange != null)
				return false;
		} else if (!genomicNTChange.equals(other.genomicNTChange))
			return false;
		if (proteinChange == null) {
			if (other.proteinChange != null)
				return false;
		} else if (!proteinChange.equals(other.proteinChange))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SmallVariantAnnotation [annoLoc=" + annoLoc + ", genomicNTChange=" + genomicNTChange + ", cdsNTChange="
				+ cdsNTChange + ", proteinChange=" + proteinChange + ", super=" + super.toString() + "]";
	}

}
