package jaywalker.report;

public class InMemoryReportFileTest extends ReportFileTestCase {

	public void setUp() throws Exception {
		super.setUp();
		reportFile = new InMemoryReportFile();
	}
	
	public void testShouldReturnReportLocationConstant() {
		assertEquals("IN MEMORY", reportFile.getParentAbsolutePath());
	}

}
