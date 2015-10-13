package de.charite.compbio.jannovar.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.data.ReferenceDictionary;

public class GenomeChangeTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	GenomePosition genomePosOneBasedForward;
	GenomePosition genomePosZeroBasedForward;
	GenomePosition genomePosZeroBasedReverse;

	@Before
	public void setUp() {
		this.genomePosOneBasedForward = new GenomePosition(refDict, Strand.FWD, 1, 123, PositionType.ONE_BASED);
		this.genomePosZeroBasedForward = new GenomePosition(refDict, Strand.FWD, 1, 122, PositionType.ZERO_BASED);
		this.genomePosZeroBasedReverse = new GenomePosition(refDict, Strand.REV, 1, 122, PositionType.ZERO_BASED);
	}

	@Test
	public void testConstructorNoUpdate() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosOneBasedForward, "A", "C");
		Assert.assertEquals(this.genomePosOneBasedForward, change.getGenomePos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandZeroRefBasesOneBased() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosOneBasedForward, "", "C", Strand.REV);
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(-1).withStrand(Strand.REV), change.getGenomePos());
		Assert.assertEquals("", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandOneRefBaseOneBased() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosOneBasedForward, "A", "C", Strand.REV);
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(0).withStrand(Strand.REV), change.getGenomePos());
		Assert.assertEquals("T", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandThreeRefBasesOneBased() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosOneBasedForward, "AAA", "CCC", Strand.REV);
		Assert.assertEquals(this.genomePosOneBasedForward.shifted(2).withStrand(Strand.REV), change.getGenomePos());
		Assert.assertEquals("TTT", change.getRef());
		Assert.assertEquals("GGG", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandZeroRefBasesZeroBased() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosZeroBasedForward, "", "C", Strand.REV);
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(-1).withStrand(Strand.REV), change.getGenomePos());
		Assert.assertEquals("", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandOneRefBaseZeroBased() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosZeroBasedForward, "A", "C", Strand.REV);
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(0).withStrand(Strand.REV), change.getGenomePos());
		Assert.assertEquals("T", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testConstructorChangeStrandThreeRefBasesZeroBased() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosZeroBasedForward, "AAA", "CCC", Strand.REV);
		Assert.assertEquals(this.genomePosZeroBasedForward.shifted(2).withStrand(Strand.REV), change.getGenomePos());
		Assert.assertEquals("TTT", change.getRef());
		Assert.assertEquals("GGG", change.getAlt());
	}

	@Test
	public void testConstructorStripLeading() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosOneBasedForward, "AAA", "AAC");
		GenomePosition expectedPos = new GenomePosition(refDict, this.genomePosOneBasedForward.getStrand(),
				this.genomePosOneBasedForward.getChr(), this.genomePosOneBasedForward.getPos() + 2, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedPos, change.getGenomePos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testConstructorStripTrailing() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosOneBasedForward, "AGG", "CGG");
		Assert.assertEquals(this.genomePosOneBasedForward, change.getGenomePos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testConstructorStripBoth() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosOneBasedForward, "GGACC", "GGCCC");
		GenomePosition expectedPos = new GenomePosition(refDict, this.genomePosOneBasedForward.getStrand(),
				this.genomePosOneBasedForward.getChr(), this.genomePosOneBasedForward.getPos() + 2, PositionType.ZERO_BASED);
		Assert.assertEquals(expectedPos, change.getGenomePos());
		Assert.assertEquals("A", change.getRef());
		Assert.assertEquals("C", change.getAlt());
	}

	@Test
	public void testWithStrandZeroBases() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosOneBasedForward, "", "C").withStrand(Strand.REV);
		GenomePosition expected = this.genomePosOneBasedForward.shifted(-1);
		GenomePosition actual = change.getGenomePos();
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testWithStrandOneBase() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosOneBasedForward, "A", "C").withStrand(Strand.REV);
		GenomePosition expected = this.genomePosOneBasedForward.shifted(0);
		GenomePosition actual = change.getGenomePos();
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("T", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testWithStrandTwoBases() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosOneBasedForward, "AA", "C").withStrand(Strand.REV);
		GenomePosition expected = this.genomePosOneBasedForward.shifted(1);
		GenomePosition actual = change.getGenomePos();
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("TT", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testWithStrandThreeBases() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosOneBasedForward, "AAA", "C").withStrand(Strand.REV);
		GenomePosition expected = this.genomePosOneBasedForward.shifted(2);
		GenomePosition actual = change.getGenomePos();
		Assert.assertEquals(expected, actual);
		Assert.assertEquals("TTT", change.getRef());
		Assert.assertEquals("G", change.getAlt());
	}

	@Test
	public void testGetGenomeIntervalForward() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosOneBasedForward, "A", "C");
		GenomeInterval genomeInterval = change.getGenomeInterval();
		GenomeInterval expectedInterval = new GenomeInterval(refDict, Strand.FWD, 1, 123, 123, PositionType.ONE_BASED);
		Assert.assertTrue(expectedInterval.equals(genomeInterval));
		Assert.assertEquals(expectedInterval, genomeInterval);
	}

	@Test
	public void testGetGenomeIntervalReverse() {
		SmallGenomeVariant change = new SmallGenomeVariant(this.genomePosZeroBasedReverse, "A", "C");
		GenomeInterval genomeInterval = change.getGenomeInterval();
		GenomeInterval expectedInterval = new GenomeInterval(refDict, Strand.REV, 1, 122, 123,
				PositionType.ZERO_BASED);
		Assert.assertTrue(expectedInterval.equals(genomeInterval));
		Assert.assertEquals(expectedInterval, genomeInterval);
	}
}
