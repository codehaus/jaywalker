package jaywalker.util;

import java.io.ByteArrayOutputStream;
import java.io.File;

import jaywalker.testutil.Path;

import org.apache.tools.ant.BuildFileTest;

public class ChainedOutputterTest extends BuildFileTest {

	private final File file = new File(Path.DIR_TEMP.getAbsolutePath()
			+ File.separator
			+ "JayWalkerTaskTest.testUnifiedReportForTest1Test2Archive"
			+ File.separator + "report.xml");

	public ChainedOutputterTest(String name) {
		super(name);
	}

	public void setUp() {
		configureProject("build-ant-test.xml");
	}

	public void testEmptyChainShouldReturnNothing() {
		ByteArrayOutputStream baos = transformFrom(null);
		assertEquals(0, baos.toString().length());
	}

	private ByteArrayOutputStream transformFrom(Outputter[] outputters) {
		if (!file.exists()) {
			executeTarget("testUnifiedReportForTest1Test2Archive");
		}
		assertTrue(file.exists());
		ResourceLocator.instance().register("report.xml", file);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ChainedOutputter outputter = new ChainedOutputter(outputters);
		outputter.write(baos);
		return baos;
	}

	public void testOneOutputterInChain() {
		Outputter[] outputters = new Outputter[] { new XsltTransformer(
				"package-dependencies-resolved-dot.xslt") };
		transformFrom(outputters);
	}

	public void testTwoOutputterInChain() {
		Outputter[] outputters = new Outputter[] {
				new XsltTransformer("package-dependencies-resolved-dot.xslt"),
				new DotOutputter("package.dot") };
		transformFrom(outputters);
	}

}
