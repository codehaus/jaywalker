package jaywalker.report;

import java.io.File;

import jaywalker.testutil.Path;

public class DefaultReportFileTest extends ReportFileTestCase {

	private File file;

	public void setUp() throws Exception {
		super.setUp();
		file = new File(Path.DIR_TEMP, "report.xml");
		if (file.exists()) {
			file.delete();
		}
		reportFile = new DefaultReportFile(file);
	}

	public void testShouldReturnReportLocation() {
		assertEquals(file.getParentFile().getAbsolutePath(), reportFile.getParentAbsolutePath());
	}

}
