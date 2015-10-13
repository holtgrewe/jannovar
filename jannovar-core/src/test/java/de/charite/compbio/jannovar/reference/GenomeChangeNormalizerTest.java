package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.data.ReferenceDictionary;

public class GenomeChangeNormalizerTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	/** transcript on forward strand */
	TranscriptModelBuilder builderForward;
	/** transcript on reverse strand */
	TranscriptModelBuilder builderReverse;
	/** projector for forward transcript */
	TranscriptProjectionDecorator projectorForward;
	/** transcript info on forward strand */
	TranscriptModel infoForward;
	/** transcript info on reverse strand */
	TranscriptModel infoReverse;
	/** projector for reverse transcript */
	TranscriptProjectionDecorator projectorReverse;

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
		this.projectorForward = new TranscriptProjectionDecorator(infoForward);
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
		this.projectorReverse = new TranscriptProjectionDecorator(infoReverse);
		// RefSeq: NM_001077195.1
	}

	@Test
	public void testForwardInsertNoNormalizationNecessaryOneBaseFirst() throws ProjectionException {
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 0, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorForward.transcriptToGenomePos(txPos);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "", "G", Strand.FWD);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeInsertion(this.infoForward, change, txPos);
		Assert.assertEquals(change, updatedChange);
	}

	@Test
	public void testForwardInsertNoNormalizationNecessaryOneBaseSecond() throws ProjectionException {
		// one base at second CDS position
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorForward.transcriptToGenomePos(txPos);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "", "A", Strand.FWD);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeInsertion(this.infoForward, change, txPos);
		Assert.assertEquals(change, updatedChange);
	}

	@Test
	public void testForwardInsertNoNormalizationNecessaryMoreBasesSecond() throws ProjectionException {
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorForward.transcriptToGenomePos(txPos);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "", "AAA", Strand.FWD);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeInsertion(this.infoForward, change, txPos);
		Assert.assertEquals(change, updatedChange);
	}

	@Test
	public void testForwardInsertNormalizationNecessaryOneBaseFirst() throws ProjectionException {
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 0, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorForward.transcriptToGenomePos(txPos);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "", "C", Strand.FWD);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeInsertion(this.infoForward, change, txPos);
		SmallGenomeVariant expectedChange = new SmallGenomeVariant(gPos.shifted(1), "", "C", Strand.FWD);
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testForwardInsertNormalizationNecessaryOneBaseSecond() throws ProjectionException {
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorForward.transcriptToGenomePos(txPos);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "", "G", Strand.FWD);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeInsertion(this.infoForward, change, txPos);
		SmallGenomeVariant expectedChange = new SmallGenomeVariant(gPos.shifted(1), "", "G", Strand.FWD);
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testForwardInsertNormalizationNecessaryMoreBasesSecond() throws ProjectionException {
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorForward.transcriptToGenomePos(txPos);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "", "GTGC", Strand.FWD);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeInsertion(this.infoForward, change, txPos);
		SmallGenomeVariant expectedChange = new SmallGenomeVariant(gPos.shifted(2), "", "GCGT", Strand.FWD);
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testReverseInsertNoNormalizationNecessaryOneBaseFirst() throws ProjectionException {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 0, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorReverse.transcriptToGenomePos(txPos).withStrand(Strand.FWD);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "", "G", Strand.REV);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeInsertion(this.infoReverse, change, txPos);
		Assert.assertEquals(change, updatedChange);
	}

	@Test
	public void testReverseInsertNoNormalizationNecessaryOneBaseSecond() throws ProjectionException {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 2, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorReverse.transcriptToGenomePos(txPos).withStrand(Strand.FWD);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "", "A", Strand.REV);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeInsertion(this.infoReverse, change, txPos);
		Assert.assertEquals(change, updatedChange);
	}

	@Test
	public void testReverseInsertNoNormalizationNecessaryMoreBasesSecond() throws ProjectionException {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 2, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorReverse.transcriptToGenomePos(txPos).withStrand(Strand.FWD);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "", "AAA", Strand.REV);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeInsertion(this.infoReverse, change, txPos);
		Assert.assertEquals(change, updatedChange);
	}

	@Test
	public void testReverseInsertNormalizationNecessaryOneBaseFirst() throws ProjectionException {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 0, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorReverse.transcriptToGenomePos(txPos).withStrand(Strand.FWD);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "", "T", Strand.FWD);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeInsertion(this.infoReverse, change, txPos);
		SmallGenomeVariant expectedChange = new SmallGenomeVariant(gPos.shifted(-1), "", "T", Strand.REV);
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testReverseInsertNormalizationNecessaryOneBaseThird() throws ProjectionException {
		// one base at second CDS position
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 3, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorReverse.transcriptToGenomePos(txPos).withStrand(Strand.FWD);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "", "T", Strand.FWD);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeInsertion(this.infoReverse, change, txPos);
		SmallGenomeVariant expectedChange = new SmallGenomeVariant(gPos.shifted(-1), "", "T", Strand.REV);
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testReverseInsertNormalizationNecessaryMoreBasesSecond() throws ProjectionException {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 17, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorReverse.transcriptToGenomePos(txPos).withStrand(Strand.FWD);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "", "AA", Strand.FWD);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeInsertion(this.infoReverse, change, txPos);
		SmallGenomeVariant expectedChange = new SmallGenomeVariant(gPos.shifted(-4), "", "AA", Strand.REV);
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testForwardDeletionNormalization() throws ProjectionException {
		TranscriptPosition txPos = new TranscriptPosition(infoForward, 1, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorForward.transcriptToGenomePos(txPos).withStrand(Strand.FWD);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "GTCACGTCCGGCGCG", "", Strand.FWD);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeDeletion(this.infoForward, change, txPos);
		SmallGenomeVariant expectedChange = new SmallGenomeVariant(gPos.shifted(1), "TCACGTCCGGCGCGG", "", Strand.FWD);
		Assert.assertEquals(expectedChange, updatedChange);
	}

	@Test
	public void testReverseDeletionNormalization() throws ProjectionException {
		TranscriptPosition txPos = new TranscriptPosition(infoReverse, 17, PositionType.ZERO_BASED);
		GenomePosition gPos = projectorReverse.transcriptToGenomePos(txPos).withStrand(Strand.FWD);
		SmallGenomeVariant change = new SmallGenomeVariant(gPos, "AA", "", Strand.FWD);
		SmallGenomeVariant updatedChange = GenomeVariantNormalizer.normalizeDeletion(this.infoReverse, change, txPos);
		SmallGenomeVariant expectedChange = new SmallGenomeVariant(gPos.shifted(-3), "AA", "", Strand.FWD);
		Assert.assertEquals(expectedChange, updatedChange);
	}

}
