package de.charite.compbio.jannovar.cmd.annotate_vcf;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;

/**
 * Run the annotate command for a symbolic duplication allele
 */
public class JannovarAnnotateVCFSymbolicDuplicationTest {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	// path to file with the first 93 lines of hg19 RefSeq (up to "Gnomon exon 459822 459929").
	private String pathToSmallSer = null;

	@Before
	public void setUp() throws URISyntaxException {
		this.pathToSmallSer = this.getClass().getResource("/hg19_small.ser").toURI().getPath();
	}

	@Test
	public void testOnSymbolicDuplicationAllele() throws IOException, URISyntaxException, CommandLineParsingException,
			HelpRequestedException, JannovarException {
		final File outFolder = tmpFolder.newFolder();
		final String inputFilePath = this.getClass().getResource("/symbolic_dup.vcf").toURI().getPath();
		String[] argv = new String[] { "annotate", "-o", outFolder.toString(), pathToSmallSer, inputFilePath };
		System.err.println(Joiner.on(" ").join(argv));
		new AnnotateVCFCommand(argv).run();
		File f = new File(outFolder.getAbsolutePath() + File.separator + "symbolic_dup.jv.vcf");
		Assert.assertTrue(f.exists());

		final File expectedFile = new File(this.getClass().getResource("/symbolic_dup.jv.vcf").toURI().getPath());
		final String expected = Files.toString(expectedFile, Charsets.UTF_8);
		final String actual = Files.toString(f, Charsets.UTF_8).replaceAll("##jannovarCommand.*", "##jannovarCommand");
		Assert.assertEquals(expected, actual);
	}

}
