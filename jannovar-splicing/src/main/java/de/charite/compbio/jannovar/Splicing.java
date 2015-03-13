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
import de.charite.compbio.jannovar.splicing.Rogan2003SplicingAcceptorMatrix;
import de.charite.compbio.jannovar.splicing.Rogan2003SplicingDonorMatrix;
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
				new VCFInfoHeaderLine("SPLICING_SCORE_REF", 2, VCFHeaderLineType.Float,
						"REF splicing score for donor/acceptor motif"), new VCFInfoHeaderLine("SPLICING_SCORE_ALT", 2,
						VCFHeaderLineType.Float, "ALT splicing score for donor/acceptor motif"));
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
						vc.getCommonInfo().putAttribute("SPLICING_SCORE_REF", score.getRefScore());
						vc.getCommonInfo().putAttribute("SPLICING_SCORE_ALT", score.getAltScore());
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

	private static SplicingScore computeScore(ReferenceSequenceFile rsf, VariantContext vc, GenomeChange change) {
		MatrixData matrixAcceptor = new Rogan2003SplicingAcceptorMatrix();
		MatrixData matrixDonor = new Rogan2003SplicingDonorMatrix();

		int delta = change.ref.length() - change.alt.length();
		int beginPos = change.getPos() - matrixAcceptor.length() + 1;
		int endPos = beginPos + delta + 2 * matrixAcceptor.length() - 1;
		System.err.println("delta=" + delta + ", beginPos=" + beginPos + ", endPos=" + endPos);

		int refBeginPos = change.getPos() - matrixAcceptor.length() + 1;
		int refEndPos = beginPos + 2 * matrixAcceptor.length() - 1;
		System.err.println("refBeginPos=" + refBeginPos + ", refEndPos=" + refEndPos);

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

		// shift matrix over sequence and get best score
		SplicingScore bestAcceptorScore = null;
		SplicingScore bestDonorScore = null;
		for (int offset = 0; offset < matrixAcceptor.length(); ++offset) {
			SplicingScore acceptorScore = new SplicingScore(SplicingScore.ScoreType.ACCEPTOR, change.getPos(), offset,
					computeOneScore(refSeq, matrixAcceptor, offset), computeOneScore(altSeq, matrixAcceptor, offset));
			if (bestAcceptorScore == null || bestAcceptorScore.compareTo(acceptorScore) < 0) {
				bestAcceptorScore = acceptorScore;
				System.err.println("BETTER ACCEPTOR SCORE:\t" + bestAcceptorScore);
				System.err.println("ACCEPTOR SEQ:\t" + altSeq.substring(offset, offset + matrixAcceptor.length()));
			}

			SplicingScore donorScore = new SplicingScore(SplicingScore.ScoreType.DONOR, change.getPos(), offset,
					computeOneScore(refSeq, matrixDonor, offset), computeOneScore(altSeq, matrixDonor, offset));
			if (bestDonorScore == null || bestDonorScore.compareTo(donorScore) < 0) {
				bestDonorScore = donorScore;
				System.err.println("BETTER DONOR SCORE:\t" + bestDonorScore);
				System.err.println("DONOR SEQ:\t" + altSeq.substring(offset, offset + matrixDonor.length()));
			}
		}

		System.err.println("BEST ACCEPTOR SCORE\t" + bestAcceptorScore);
		System.err.println("ALT ACCEPTOR SEQ   \t" + altSeq.charAt(bestAcceptorScore.getPos() - change.getPos()));
		System.err.println("BEST DONOR SCORE   \t" + bestDonorScore);
		System.err.println("ALT DONOR SEQ      \t" + altSeq.charAt(bestDonorScore.getPos() - change.getPos()));

		return bestDonorScore.compareTo(bestAcceptorScore) < 0 ? bestAcceptorScore : bestDonorScore;
	}

	private static double computeOneScore(String seq, MatrixData matrix, int offset) {
		return matrix.getScore(seq, offset);
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
