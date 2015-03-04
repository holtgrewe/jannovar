package de.charite.compbio.jannovar;

import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.reference.ReferenceSequenceFile;
import htsjdk.samtools.reference.ReferenceSequenceFileFactory;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeaderLine;

import java.io.File;
import java.util.Collection;

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
				JannovarOptions.JANNOVAR_VERSION), new VCFHeaderLine("jannovarCommand", Joiner.on(' ').join(args)));
		VariantContextWriter writer = VariantContextWriterConstructionHelper.openVariantContextWriter(
				reader.getFileHeader(), pathOutVCF, fields, additionalLines);

		System.err.println("Creating annotator object...");
		VariantContextAnnotator annotator = new VariantContextAnnotator(data.refDict, data.chromosomes,
				new VariantContextAnnotator.Options(fields, true, true, true));

		int count = 0;
		for (VariantContext vc : reader) {
			try {
				ImmutableList<AnnotationList> lst = annotator.buildAnnotationList(vc);
				if (containsSplicing(lst)) {
					System.err.println("Found putative splicing variant: " + vc);
					System.err.println("\t" + lst);
					++count;
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

	/**
	 * @param lst
	 *            list of {@link AnnotationList} objects with annotations for a variant
	 * @return <code>true</code> if <code>lst</code> contains a splicing annotation
	 */
	private static boolean containsSplicing(Collection<AnnotationList> lst) {
		for (AnnotationList annos : lst) {
			for (Annotation anno : annos) {
				if (anno.effects.contains(VariantEffect.SPLICE_ACCEPTOR_VARIANT)
						|| anno.effects.contains(VariantEffect.SPLICE_DONOR_VARIANT)
						|| anno.effects.contains(VariantEffect.SPLICE_REGION_VARIANT))
					return true;
			}
		}
		return false;
	}
}
