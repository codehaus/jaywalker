package jaywalker.report;

import java.io.File;

import jaywalker.testutil.Path;
import jaywalker.util.ResourceLocator;
import jaywalker.util.ResourceNotFoundException;
import junit.framework.TestCase;

public class ReportFileFactoryTest extends TestCase {
	
	
	public void setUp() throws Exception {
		super.setUp();
		ResourceLocator.instance().register("client", "ant");
	}

	public void tearDown() {
		ResourceLocator.instance().clear();
	}

	public void testShouldCreateInMemoryReportFile() {
		System.setProperty("ReportFile", "jaywalker.report.InMemoryReportFile");
		ReportFile reportFile = new ReportFileFactory().create("report.xml");
		assertEquals(InMemoryReportFile.class, reportFile.getClass());
	}

	public void testShouldCreateDefaultReportFile() {
		System.setProperty("ReportFile", "jaywalker.report.DefaultReportFile");
		ResourceLocator.instance().register("outDir", Path.DIR_TEMP);
		assertFileDoesNotExist(Path.DIR_TEMP, "report.xml");
		ReportFile reportFile = new ReportFileFactory().create("report.xml");
		assertEquals(DefaultReportFile.class, reportFile.getClass());
	}

	public void testShouldCreateDefaultReportFileOnMissingSystemProperty() {
		ResourceLocator.instance().register("outDir", Path.DIR_TEMP);
		assertFileDoesNotExist(Path.DIR_TEMP, "report.xml");
		ReportFile reportFile = new ReportFileFactory().create("report.xml");
		assertEquals(DefaultReportFile.class, reportFile.getClass());
	}

	public void testShouldThrowExceptionWhenOutDirIsMissing() {
		try {
			assertFileDoesNotExist(Path.DIR_TEMP, "report.xml");
			ReportFile reportFile = new ReportFileFactory().create("report.xml");
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
