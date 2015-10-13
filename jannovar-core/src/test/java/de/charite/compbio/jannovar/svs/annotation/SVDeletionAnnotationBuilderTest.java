package de.charite.compbio.jannovar.svs.annotation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.annotation.SmallVariantAnnotation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.annotation.builders.SNVAnnotationBuilder;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.HG19RefDictBuilder;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;
import de.charite.compbio.jannovar.reference.TranscriptModelFactory;
import de.charite.compbio.jannovar.svs.SVDeletion;
import de.charite.compbio.jannovar.svs.annotation.SVDeletionAnnotationBuilder;

public class SVDeletionAnnotationBuilderTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();
	/** transcript on forward strand */
	TranscriptModelBuilder builderForward;
	/** transcript on reverse strand */
	TranscriptModelBuilder builderReverse;
	/** transcript info on forward strand */
	TranscriptModel infoForward;
	/** transcript info on reverse strand */
	TranscriptModel infoReverse;

	@Before
	public void setUp() {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001anx.3	chr1	+	6640062	6649340	6640669	6649272	11	6640062,6640600,6642117,6645978,6646754,6647264,6647537,6648119,6648337,6648815,6648975,	6640196,6641359,6642359,6646090,6646847,6647351,6647692,6648256,6648502,6648904,6649340,	P10074	uc001anx.3");
		this.builderForward
				.setSequence("cgtcacgtccggcgcggagacggtggagtctccgcactgtcggcggggtacgcatagccgggcactaggttcgtgggctgtggaggcgacggagcagggggccagtggggccagctcagggaggacctgcctgggagctttctcttgcataccctcgcttaggctggccggggtgtcacttctgcctccctgccctccagaccatggacggctccttcgtccagcacagtgtgagggttctgcaggagctcaacaagcagcgggagaagggccagtactgcgacgccactctggacgtggggggcctggtgtttaaggcacactggagtgtccttgcctgctgcagtcactttttccagagcctctacggggatggctcagggggcagtgtcgtcctccctgctggcttcgctgagatctttggcctcttgttggactttttctacactggtcacctcgctctcacctcagggaaccgggatcaggtgctcctggcagccagggagttgcgagtgccagaggccgtagagctgtgccagagcttcaagcccaaaacttcagtgggacaggcagcaggtggccagagtgggctggggccccctgcctcccagaatgtgaacagccacgtcaaggagccggcaggcttggaagaagaggaagtttcgaggactctgggtctagtccccagggatcaggagcccagaggcagtcatagtcctcagaggccccagctccattccccagctcagagtgagggcccctcctccctctgtgggaaactgaagcaggccttgaagccttgtccccttgaggacaagaaacccgaggactgcaaagtgcccccaaggcccttagaggctgaaggtgcccagctgcagggcggcagtaatgagtgggaagtggtggttcaagtggaggatgatggggatggcgattacatgtctgagcctgaggctgtgctgaccaggaggaagtcaaatgtaatccgaaagccctgtgcagctgagccagccctgagcgcgggctccctagcagctgagcctgctgagaacagaaaaggtacagcggtgccggtcgaatgccccacatgtcataaaaagttcctcagcaaatattatctaaaagtccacaacaggaaacatactggggagaaaccctttgagtgtcccaaatgtgggaagtgttactttcggaaggagaacctcctggagcatgaagcccggaattgcatgaaccgctcggaacaggtcttcacgtgctctgtgtgccaggagacattccgccgaaggatggagctgcgggtgcacatggtgtctcacacaggggagatgccctacaagtgttcctcctgctcccagcagttcatgcagaagaaggacttgcagagccacatgatcaaacttcatggagcccccaagccccatgcatgccccacctgtgccaagtgcttcctgtctcggacagagctgcagctgcatgaagctttcaagcaccgtggtgagaagctgtttgtgtgtgaggagtgtgggcaccgggcctcgagccggaatggcctgcagatgcacatcaaggccaagcacaggaatgagaggccacacgtatgtgagttctgcagccacgccttcacccaaaaggccaatctcaacatgcacctgcgcacacacacgggtgagaagcccttccagtgccacctctgtggcaagaccttccgaacccaagccagcctggacaagcacaaccgcacccacaccggggaaaggcccttcagttgcgagttctgtgaacagcgcttcactgagaaggggcccctcctgaggcacgtggccagccgccatcaggagggccggccccacttctgccagatatgcggcaagaccttcaaagccgtggagcaactgcgtgtgcacgtcagacggcacaagggggtgaggaagtttgagtgcaccgagtgtggctacaagtttacccgacaggcccacctgcggaggcacatggagatccacgaccgggtagagaactacaacccgcggcagcgcaagctccgcaacctgatcatcgaggacgagaagatggtggtggtggcgctgcagccgcctgcagagctggaggtgggctcggcggaggtcattgtggagtccctggcccagggcggcctggcctcccagctccccggccagagactgtgtgcagaggagagcttcaccggcccaggtgtcctggagccctccctcatcatcacagctgctgtccccgaggactgtgacacatagcccattctggccaccagagcccacttggccccacccctcaataaaccgtgtggctttggactctcgtaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ZBTB48");
		this.infoForward = builderForward.build();
		// RefSeq: NM_005341.3

		this.builderReverse = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001bgu.3	chr1	-	23685940	23696357	23688461	23694498	4	23685940,23693534,23694465,23695858,	23689714,23693661,23694558,23696357,	Q9C0F3	uc001bgu.3");
		this.builderReverse
				.setSequence("aataagctgctatattctttttccatcacttccctctccaaggctacagcgagctgggagctcttccccacgcagaatgcctgctttccccagtgctcgacttccattgtctaattccctcatcctggctggggaaagggagagctgcgagtcctcccgttccgaggaactccagctgaatgcagcttagttgctggtggtttctcggccagcctctgtggtctcagggatctgcctatgagcctgtggtttctgagctgcctgcgagtctgaggcctcgggaatctgagtctttaggatcagcctacgatatctgggcttcgcctgcaagtctacgaattcgagatctacctgcgggtctgagacctccgggacctgcccgtgctctctagaatcttcctgaacgccaggtctgagagaacgctgcggctctggaacccgttcgcggtctctcaggttttggagacgacgatctagtggatcttttgcgggacaggagcgctgtctgctagctgcttttcctgctctctctccctggaggcgaacccttgtgctcgagatggcagccaccctgctcatggctgggtcccaggcacctgtgacgtttgaagatatggccatgtatctcacccgggaagaatggagacctctggacgctgcacagagggacctttaccgggatgttatgcaggagaattatggaaatgttgtctcactagattttgagatcaggagtgagaacgaggtaaatcccaagcaagagattagtgaagatgtacaatttgggactacatctgaaagacctgctgagaatgctgaggaaaatcctgaaagtgaagagggctttgaaagcggagataggtcagaaagacaatggggagatttaacagcagaagagtgggtaagctatcctctccaaccagtcactgatctacttgtccacaaagaagtccacacaggcatccgctatcatatatgttctcattgtggaaaggccttcagtcagatctcagaccttaatcgacatcagaagacccacactggagacagaccctataaatgttatgaatgtggaaaaggcttcagtcgcagctcacaccttattcagcatcaaagaacacatactggggagaggccttatgactgtaacgagtgtgggaaaagttttggaagaagttctcacctgattcagcatcagacaatccacactggagagaagcctcacaaatgtaatgagtgtggaaaaagtttctgccgtctctctcacctaatccaacaccaaaggacccacagtggtgagaaaccctatgagtgtgaggagtgtgggaaaagcttcagccggagctctcacctagctcagcaccagaggacccacacgggtgagaaaccttatgaatgtaacgaatgtggccgaggcttcagtgagagatctgatctcatcaaacactatcgagtccacacaggggagaggccctacaagtgtgatgagtgtgggaagaatttcagtcagaactccgaccttgtgcgtcatcgcagagcccacacgggagagaagccataccactgtaacgaatgtggggaaaatttcagccgcatctcacacttggttcagcaccagagaactcacactggagagaagccatatgaatgcaatgcttgtgggaaaagcttcagccggagctctcatctcatcacacaccagaaaattcacactggagagaagccttatgagtgtaatgagtgttggcgaagctttggtgaaaggtcagatctaattaaacatcagagaacccacacaggggagaagccctacgagtgtgtgcagtgtgggaaaggtttcacccagagctccaacctcatcacacatcaaagagttcacacgggagagaaaccttatgaatgtaccgaatgtgagaagagtttcagcaggagctcagctcttattaaacataagagagttcatacggactaagctgtaattatgatggctgagaaatgattcatttgaagatacaattttatttgatatcaatgaacgccctcaagactgagctgcttttatcatactctcctagttgtgggccacgatttaaaccatcagagatgacaagccatttgaaattctgaccctcagctttgggaatgttatctcctccaaaatggtgatttttattcactcaatgggttacttcattaaaagcagccccacaagtaactggaaatctgaagaccaggggacaaatgctggtgaatgcttaggcctggaaatggagtaaatctttcaatgttattttctcccatccttggcccaaggaactatgctaagtgaaacgtgggactgtaatagggtggtaatggctgctttggaaaaaggcaactagagactctgcctaaattgccacacctattcacacaccatagtagttgggcacacacatcttcccttccaaagggctttttccttgagttgctcatgcatttgtatcttttccatcttcctgagggcaagattttgcacgatgaaggcaatgattgtaacttttctccttctcattgtttctaattagctcctttaaagcttgcatctttgtgaaggctaactgaagatacggttggaaaggaaaaatgagacacaggtttggggaccaaggacccatcaatgatggtgactttagcagaagatgcccacagttattactgccattaatcagatttatgaattttctttggggatcactatagggaatattgtatagaaaatatcttcaagaaaagataggaccatcagtgacagttaagtgtaaggagcaagtggaattgagtccttcagggaaggaaccacagagtcccttcccaaggaatgtaggtcgtttctgtgttctttcccttctaatctttaagatcaactcttcctatcctgctaactctaagatttgataagggccacatcccagtgtttatcttagcttgcatcagggcatgtgtatgtacagtaatgtgtattcctgtggtttttctaatagaaactgaatttacagagacttagcatgttcttgggtgatgtgagtcatgtgacagaagtacagacataactccaatgtgagaaatgtccttttttcattatggaaaataatttaaacactagtgctttagtgtgcactctcctgtaaggtctgtctttgtacagagctaagcacttgtttgtatgtgtttgtcaattgtggaagataatgaccagacaaataggtcgattgtcctattctcagaatgaattatcttctatggtaatgaagaactctttggcttagtcagaaggaattaacgaacctcggtaggaatgtatttccatcctcccaccctacagatataagaggttaaaataacagttcgcccaatttaagcccagtagtgtcagttttcctaatctcagtccaggtaggaattaagaaatatctcaagtgttgatgctatccaagcatgttggggtggaagggaattggtgcccagaaaatgggactggagtgaggaatatcttttcttttgagagtacccccagtttatttctactgtgctttattgctactgttctttattgtgaatgttgtaacattttaaaaatgttttgccatagctttttaggacttggtgttaaaggagccagtggtctctctgggtgggtactataatgagttattgtgacccacagctgtgtgggaccacatcacttgttaataacacaacctttaaagtaacccatcttccaggggggttccttcatgttgccactcctttttaaggacaaactcaggcaaggagcatgtttttttgttatttacaaaatctagcagactgtgggtatccatattttaattgtcgggtgacacatgttcttggtaactaaactcaaatatgtcttttctcatatatgttgctgatggttttaataaatgtcaaagttctcctgttgcttctgtgagccactatgggtatcagcttgggagtggccatagatgaccgcatttccatgacctaactgtatttcacccccttttccttccctactgttcttgccccaccccaaccagttcctgctgctgcttttggcttcttggaggtgaagggcttaaaacaaggcttctaagcacccagctatctccatacatgaacaatctagctgggaaacttaagggacaagggccacaccagctgtctcctctttctgccaattgttgcccgtttgctgtgttgaactttgtatagaactcatgcatcagactcccttcactaatgctttttgcatgccttctgctcccaagtccctggctgcctctgcacatcccgtgaacactttgtgcctgttttctatggttgtggagaattaatgaacaaatcaatatgtagaacagttttccttatggtattggtcacagttatcctagtgtttgtattattctaacaatattctataattaaaaatataatttttaaagtca"
						.toUpperCase());
		this.builderReverse.setGeneSymbol("ZNF436");
		this.infoReverse = builderReverse.build();
		// RefSeq: NM_001077195.1
	}

	@Test
	public void testForwardTranscriptAblation() throws InvalidGenomeVariant {
		SVDeletion variant = new SVDeletion(new GenomePosition(refDict, Strand.FWD, 1, 6640000), "A", "<DEL>", -10, 10,
				20000, -10, 10, "");
		SVDeletionAnnotationBuilder builder = new SVDeletionAnnotationBuilder(infoForward, variant,
				new AnnotationBuilderOptions());
		StructuralVariantAnnotation anno = builder.build();
		Assert.assertEquals(
				"<DEL>|transcript_ablation&copy_number_loss&copy_number_decrease&structural_variant&coding_transcript_variant|HIGH|ZBTB48||transcript|uc001anx.3|Coding||||||||",
				anno.toVCFAnnoString(variant.getAlt()));
	}

	@Test
	public void testForwardExonDeletion() throws InvalidGenomeVariant {
		SVDeletion variant = new SVDeletion(new GenomePosition(refDict, Strand.FWD, 1, 6640000), "A", "<DEL>", -10, 10,
				200, -10, 10, "");
		SVDeletionAnnotationBuilder builder = new SVDeletionAnnotationBuilder(infoForward, variant,
				new AnnotationBuilderOptions());
		StructuralVariantAnnotation anno = builder.build();
		Assert.assertEquals(
				"<DEL>|exon_loss_variant&copy_number_loss&copy_number_decrease&structural_variant&coding_transcript_variant|HIGH|ZBTB48||transcript|uc001anx.3|Coding||||||||",
				anno.toVCFAnnoString(variant.getAlt()));
	}

	@Test
	public void testForwardFeatureTruncation() throws InvalidGenomeVariant {
		SVDeletion variant = new SVDeletion(new GenomePosition(refDict, Strand.FWD, 1, 6640000), "A", "<DEL>", -10, 10,
				100, -10, 10, "");
		SVDeletionAnnotationBuilder builder = new SVDeletionAnnotationBuilder(infoForward, variant,
				new AnnotationBuilderOptions());
		StructuralVariantAnnotation anno = builder.build();
		Assert.assertEquals(
				"<DEL>|feature_truncation&copy_number_loss&copy_number_decrease&structural_variant&coding_transcript_variant|HIGH|ZBTB48||transcript|uc001anx.3|Coding||||||||",
				anno.toVCFAnnoString(variant.getAlt()));
	}

	@Test
	public void testForwardFeatureUpstream() throws InvalidGenomeVariant {
		SVDeletion variant = new SVDeletion(new GenomePosition(refDict, Strand.FWD, 1, 6640000), "A", "<DEL>", -10, 10,
				50, -10, 10, "");
		SVDeletionAnnotationBuilder builder = new SVDeletionAnnotationBuilder(infoForward, variant,
				new AnnotationBuilderOptions());
		StructuralVariantAnnotation anno = builder.build();
		Assert.assertEquals(
				"<DEL>|copy_number_decrease&upstream_gene_variant&structural_variant&coding_transcript_variant|MODIFIER|ZBTB48||transcript|uc001anx.3|Coding|||||||2|",
				anno.toVCFAnnoString(variant.getAlt()));
	}

	@Test
	public void testForwardFeatureDownstream() throws InvalidGenomeVariant {
		SVDeletion variant = new SVDeletion(new GenomePosition(refDict, Strand.FWD, 1, 6649341), "A", "<DEL>", -10, 10,
				100, -10, 10, "");
		SVDeletionAnnotationBuilder builder = new SVDeletionAnnotationBuilder(infoForward, variant,
				new AnnotationBuilderOptions());
		StructuralVariantAnnotation anno = builder.build();
		Assert.assertEquals(
				"<DEL>|copy_number_decrease&downstream_gene_variant&structural_variant&coding_transcript_variant|MODIFIER|ZBTB48||transcript|uc001anx.3|Coding|||||||11|",
				anno.toVCFAnnoString(variant.getAlt()));
	}

	@Test
	public void testForwardFeatureIntergenic() throws InvalidGenomeVariant {
		SVDeletion variant = new SVDeletion(new GenomePosition(refDict, Strand.FWD, 1, 6640340), "A", "<DEL>", -10, 10,
				100, -10, 10, "");
		SVDeletionAnnotationBuilder builder = new SVDeletionAnnotationBuilder(infoForward, variant,
				new AnnotationBuilderOptions());
		StructuralVariantAnnotation anno = builder.build();
		Assert.assertEquals(
				"<DEL>|copy_number_decrease&intergenic_variant&structural_variant&coding_transcript_variant|MODIFIER|ZBTB48||transcript|uc001anx.3|Coding|||||||-8990|",
				anno.toVCFAnnoString(variant.getAlt()));
	}

	@Test
	public void testReverseTranscriptAblation() throws InvalidGenomeVariant {
		SVDeletion variant = new SVDeletion(new GenomePosition(refDict, Strand.FWD, 1, 23685900), "A", "<DEL>", -10,
				10, 20000, -10, 10, "");
		SVDeletionAnnotationBuilder builder = new SVDeletionAnnotationBuilder(infoReverse, variant,
				new AnnotationBuilderOptions());
		StructuralVariantAnnotation anno = builder.build();
		Assert.assertEquals(
				"<DEL>|transcript_ablation&copy_number_loss&copy_number_decrease&structural_variant&coding_transcript_variant|HIGH|ZNF436||transcript|uc001bgu.3|Coding||||||||",
				anno.toVCFAnnoString(variant.getAlt()));
	}

	@Test
	public void testReverseExonDeletion() throws InvalidGenomeVariant {
		SVDeletion variant = new SVDeletion(new GenomePosition(refDict, Strand.FWD, 1, 23685900), "A", "<DEL>", -10,
				10, 5000, -10, 10, "");
		SVDeletionAnnotationBuilder builder = new SVDeletionAnnotationBuilder(infoReverse, variant,
				new AnnotationBuilderOptions());
		StructuralVariantAnnotation anno = builder.build();
		Assert.assertEquals(
				"<DEL>|exon_loss_variant&copy_number_loss&copy_number_decrease&structural_variant&coding_transcript_variant|HIGH|ZNF436||transcript|uc001bgu.3|Coding||||||||",
				anno.toVCFAnnoString(variant.getAlt()));
	}

	@Test
	public void testReverseFeatureTruncation() throws InvalidGenomeVariant {
		SVDeletion variant = new SVDeletion(new GenomePosition(refDict, Strand.FWD, 1, 23685940), "A", "<DEL>", -10,
				10, 200, -10, 10, "");
		SVDeletionAnnotationBuilder builder = new SVDeletionAnnotationBuilder(infoReverse, variant,
				new AnnotationBuilderOptions());
		StructuralVariantAnnotation anno = builder.build();
		Assert.assertEquals(
				"<DEL>|feature_truncation&copy_number_loss&copy_number_decrease&structural_variant&coding_transcript_variant|HIGH|ZNF436||transcript|uc001bgu.3|Coding||||||||",
				anno.toVCFAnnoString(variant.getAlt()));
	}

	@Test
	public void testReverseFeatureUpstream() throws InvalidGenomeVariant {
		SVDeletion variant = new SVDeletion(new GenomePosition(refDict, Strand.FWD, 1, 23696457), "A", "<DEL>", -10,
				10, 50, -10, 10, "");
		SVDeletionAnnotationBuilder builder = new SVDeletionAnnotationBuilder(infoReverse, variant,
				new AnnotationBuilderOptions());
		StructuralVariantAnnotation anno = builder.build();
		Assert.assertEquals(
				"<DEL>|copy_number_decrease&upstream_gene_variant&structural_variant&coding_transcript_variant|MODIFIER|ZNF436||transcript|uc001bgu.3|Coding|||||||10528|",
				anno.toVCFAnnoString(variant.getAlt()));
	}

	@Test
	public void testReverseFeatureDownstream() throws InvalidGenomeVariant {
		SVDeletion variant = new SVDeletion(new GenomePosition(refDict, Strand.FWD, 1, 23685540), "A", "<DEL>", -10,
				10, 100, -10, 10, "");
		SVDeletionAnnotationBuilder builder = new SVDeletionAnnotationBuilder(infoReverse, variant,
				new AnnotationBuilderOptions());
		StructuralVariantAnnotation anno = builder.build();
		Assert.assertEquals(
				"<DEL>|copy_number_decrease&downstream_gene_variant&structural_variant&coding_transcript_variant|MODIFIER|ZNF436||transcript|uc001bgu.3|Coding|||||||-10706|",
				anno.toVCFAnnoString(variant.getAlt()));
	}

	@Test
	public void testReverseFeatureIntergenic() throws InvalidGenomeVariant {
		SVDeletion variant = new SVDeletion(new GenomePosition(refDict, Strand.FWD, 1, 23680940), "A", "<DEL>", -10,
				10, 100, -10, 10, "");
		SVDeletionAnnotationBuilder builder = new SVDeletionAnnotationBuilder(infoReverse, variant,
				new AnnotationBuilderOptions());
		StructuralVariantAnnotation anno = builder.build();
		Assert.assertEquals(
				"<DEL>|copy_number_decrease&intergenic_variant&structural_variant&coding_transcript_variant|MODIFIER|ZNF436||transcript|uc001bgu.3|Coding|||||||-15306|",
				anno.toVCFAnnoString(variant.getAlt()));
	}
}
