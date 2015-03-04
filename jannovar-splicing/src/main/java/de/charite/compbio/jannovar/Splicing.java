package de.charite.compbio.jannovar;

import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import java.io.File;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationList;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.htsjdk.InfoFields;
import de.charite.compbio.jannovar.htsjdk.InvalidCoordinatesException;
import de.charite.compbio.jannovar.htsjdk.VariantContextAnnotator;
import de.charite.compbio.jannovar.htsjdk.VariantContextWriterConstructionHelper;
import de.charite.compbio.jannovar.io.JannovarData;
import de.charite.compbio.jannovar.io.JannovarDataSerializer;
import de.charite.compbio.jannovar.io.SerializationException;
import de.charite.compbio.jannovar.reference.GenomeChange;
import de.charite.compbio.jannovar.splicing.MatrixData;
import de.charite.compbio.jannovar.splicing.SchneiderSplicingAcceptorMatrix;
import de.charite.compbio.jannovar.splicing.SchneiderSplicingDonorMatrix;
import de.charite.compbio.jannovar.splicing.SplicingScore;

public class Splicing {
	public static void main(String[] args) {
		if (args.length != 4) {
			System.err.println("USAGE: Splicing REF.fasta IN.ser.gz IN.vcf OUT.vcf");
			System.exit(1);
		}

		String pathRef = args[0];
		String pathDB = args[1];
		String pathInVCF = args[2];
		String pathOutVCF = args[3];

		System.err.println("REF PATH\t" + pathRef);
		System.err.println("DB PATH \t" + pathDB);
		System.err.println("IN VCF  \t" + pathInVCF);
		System.err.println("OUT VCF \t" + pathOutVCF);

		System.err.println("Loading Reference Index...");
		ReferenceSequenceFile rsf = ReferenceSequenceFileFactory.getReferenceSequenceFile(new File(pathRef), true);
		if (!rsf.isIndexed()) {
			System.err.println("ERROR: file is not indexed!");
			System.exit(1);
		}

		System.err.println("Opening input file...");
		VCFFileReader reader = new VCFFileReader(new File(pathInVCF), false);

		System.err.println("Checking VCF vs. FASTA file...");
		int numWarnings = 0;
		for (SAMSequenceRecord record : reader.getFileHeader().getSequenceDictionary().getSequences())
			if (rsf.getSequenceDictionary().getSequence(record.getSequenceName()) == null) {
				System.err.println("WARNING: could not find sequence " + record.getSequenceName() + " in FASTA file.");
				numWarnings += 1;
			}
		if (numWarnings == 0)
			System.err.println("Sequence names in FASTA and VCF file are the same.");

		System.err.println("Loading database...");
		JannovarData data = null;
		try {
			data = new JannovarDataSerializer(pathDB).load();
		} catch (SerializationException e) {
			System.err.println("Problem loading database: " + e.getMessage());
			System.exit(1);
		}

		System.err.println("Opening output file...");
		final InfoFields fields = InfoFields.build(true, false);
		ImmutableSet<VCFHeaderLine> additionalLines = ImmutableSet.of(new VCFHeaderLine("jannovarVersion",
				JannovarOptions.JANNOVAR_VERSION), new VCFHeaderLine("jannovarCommand", Joiner.on(' ').join(args)),
				new VCFInfoHeaderLine("SPLICING_SCORE", 2, VCFHeaderLineType.Float,
						"Splicing score for donor/acceptor motif"));
		VariantContextWriter writer = VariantContextWriterConstructionHelper.openVariantContextWriter(
				reader.getFileHeader(), pathOutVCF, fields, additionalLines);

		System.err.println("Creating annotator object...");
		VariantContextAnnotator annotator = new VariantContextAnnotator(data.refDict, data.chromosomes,
				new VariantContextAnnotator.Options(fields, true, true, true));

		int count = 0;
		for (VariantContext vc : reader) {
			try {
				ImmutableList<AnnotationList> lst = annotator.buildAnnotationList(vc);
				for (int alleleID = 0; alleleID < lst.size(); ++alleleID) {
					AnnotationList annos = lst.get(alleleID);
					if (containsSplicing(annos)) {
						System.err.println("Found putative splicing variant: " + vc);
						System.err.println("\t" + lst);
						++count;

						SplicingScore score = computeScore(rsf, vc, annos.getChange());
						vc.getCommonInfo().putAttribute("SPLICING_SCORE",
								Joiner.on(',').join(score.getDonorScore(), score.getAcceptorScore()));
						System.err.println("\tScore = " + score);
					}
				}
				annotator.applyAnnotations(vc, lst);
			} catch (InvalidCoordinatesException e) {
				annotator.putErrorAnnotation(vc, ImmutableSet.of(e.getAnnotationMessage()));
			}
			writer.add(vc);
		}

		System.err.println("Number of putative splicing variants: " + count);

		System.err.println("Closing file again...");
		reader.close();
		writer.close();

		System.err.println("All done. Have a nice day...");
	}

	// TODO(holtgrew): We must shift the whole matrix over the position instead of considering it to be on the center
	// only.

	private static SplicingScore computeScore(ReferenceSequenceFile rsf, VariantContext vc, GenomeChange change) {
		SchneiderSplicingAcceptorMatrix matrixAcceptor = new SchneiderSplicingAcceptorMatrix();
		SchneiderSplicingDonorMatrix matrixDonor = new SchneiderSplicingDonorMatrix();

		int delta = change.ref.length() - change.alt.length();
		int beginPos = change.getPos() + matrixAcceptor.getMinOffset();
		int endPos = beginPos + delta + matrixAcceptor.length();

		int refBeginPos = beginPos;
		int refEndPos = beginPos + matrixAcceptor.length();

		String refSeq = new String(rsf.getSubsequenceAt(vc.getChr(), refBeginPos + 1, refEndPos).getBases());
		String altRawSeq = new String(rsf.getSubsequenceAt(vc.getChr(), beginPos + 1, endPos).getBases());
		StringBuilder altBuilder = new StringBuilder(altRawSeq);
		int start = change.getPos() - beginPos;
		int end = start + change.ref.length();
		System.err.println("start=" + start + ", end=" + end);
		altBuilder.delete(start, end);
		altBuilder.insert(start, change.alt);
		String altSeq = altBuilder.toString();

		System.err.println("GENOME CHANGE\t" + change);
		System.err.println("REF SEQ      \t" + refSeq);
		System.err.println("ALT RAW SEQ  \t" + altRawSeq);
		System.err.println("ALT SEQ      \t" + altSeq);

		return new SplicingScore(computeOneScore(refSeq, matrixDonor), computeOneScore(altSeq, matrixAcceptor),
				computeOneScore(altSeq, matrixDonor), computeOneScore(altSeq, matrixAcceptor));
	}

	private static double computeOneScore(String seq, MatrixData matrix) {
		return matrix.getScore(seq);
	}

	/**
	 * @param lst
	 *            {@link AnnotationList} object with annotations for a variant
	 * @return <code>true</code> if <code>lst</code> contains a splicing annotation
	 */
	private static boolean containsSplicing(AnnotationList annos) {
		for (Annotation anno : annos) {
			if (anno.effects.contains(VariantEffect.SPLICE_ACCEPTOR_VARIANT)
					|| anno.effects.contains(VariantEffect.SPLICE_DONOR_VARIANT)
					|| anno.effects.contains(VariantEffect.SPLICE_REGION_VARIANT))
				return true;
		}
		return false;
	}
}
