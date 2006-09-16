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
			FileDecorator reportFile = new FileDecoratorFactory().create(
					Path.DIR_TEMP, "report.xml");
			assertEquals(InMemoryFileDecorator.class, reportFile.getClass());
		} finally {
			System.setProperty("ReportFile", "");
		}
	}

	public void testShouldCreateDefaultReportFile() {
		try {
			System.setProperty("ReportFile",
					"jaywalker.report.DefaultFileDecorator");
			assertFileDoesNotExist(Path.DIR_TEMP, "report.xml");
			FileDecorator reportFile = new FileDecoratorFactory().create(
					Path.DIR_TEMP, "report.xml");
			assertEquals(DefaultFileDecorator.class, reportFile.getClass());
		} finally {
			System.setProperty("ReportFile", "");
		}
	}

	public void testShouldCreateDefaultReportFileOnMissingSystemProperty() {
		assertFileDoesNotExist(Path.DIR_TEMP, "report.xml");
		FileDecorator reportFile = new FileDecoratorFactory().create(Path.DIR_TEMP,
				"report.xml");
		assertEquals(DefaultFileDecorator.class, reportFile.getClass());
	}

	private void assertFileDoesNotExist(File dir, String filename) {
		File file = new File(dir, filename);
		if (file.exists()) {
			file.delete();
		}
	}

}
