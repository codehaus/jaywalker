package jaywalker.report;

import jaywalker.util.ResourceLocator;

public class InMemoryReportFileTest extends ReportFileTestCase {

	public void setUp() throws Exception {
		super.setUp();
		ResourceLocator.instance().register("client", "ant");
		reportFile = new InMemoryReportFile("report.xml");
	}

	public void tearDown() throws Exception {
		super.tearDown();
		ResourceLocator.instance().clear();
	}

	public void testShouldReturnReportLocationConstant() {
		assertEquals("IN MEMORY", reportFile.getParentAbsolutePath());
	}

}
