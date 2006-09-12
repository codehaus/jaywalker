package jaywalker.util;

import java.io.File;

import jaywalker.classlist.JayWalkerTestCase;
import jaywalker.testutil.Path;

public class FileDecoratorFactoryTest extends JayWalkerTestCase {

	public FileDecoratorFactoryTest(String name) {
		super(name);
	}

	public void testShouldCreateInMemoryReportFile() {
		try {
			System.setProperty("ReportFile",
					"jaywalker.util.InMemoryFileDecorator");
			ResourceLocator.instance().register("outDir", Path.DIR_TEMP);
			FileDecorator reportFile = new FileDecoratorFactory()
					.create("report.xml");
			assertEquals(InMemoryFileDecorator.class, reportFile.getClass());
		} finally {
			System.setProperty("ReportFile", "");
		}
	}

	public void testShouldCreateDefaultReportFile() {
		try {
			System.setProperty("ReportFile",
					"jaywalker.report.DefaultFileDecorator");
			ResourceLocator.instance().register("outDir", Path.DIR_TEMP);
			assertFileDoesNotExist(Path.DIR_TEMP, "report.xml");
			FileDecorator reportFile = new FileDecoratorFactory()
					.create("report.xml");
			assertEquals(DefaultFileDecorator.class, reportFile.getClass());
		} finally {
			System.setProperty("ReportFile", "");
		}
	}

	public void testShouldCreateDefaultReportFileOnMissingSystemProperty() {
		ResourceLocator.instance().register("outDir", Path.DIR_TEMP);
		assertFileDoesNotExist(Path.DIR_TEMP, "report.xml");
		FileDecorator reportFile = new FileDecoratorFactory()
				.create("report.xml");
		assertEquals(DefaultFileDecorator.class, reportFile.getClass());
	}

	public void testShouldThrowExceptionWhenOutDirIsMissing() {
		try {
			assertFileDoesNotExist(Path.DIR_TEMP, "report.xml");
			new FileDecoratorFactory().create("report.xml");
			fail("ResourceNotFoundException should have been thrown");
		} catch (ResourceNotFoundException e) {
		}
	}

	private void assertFileDoesNotExist(File dir, String filename) {
		File file = new File(dir, filename);
		if (file.exists()) {
			file.delete();
		}
	}

}
