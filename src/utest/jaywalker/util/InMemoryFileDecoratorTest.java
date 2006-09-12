package jaywalker.util;

import jaywalker.util.InMemoryFileDecorator;
import jaywalker.util.ResourceLocator;

public class InMemoryFileDecoratorTest extends FileDecoratorTestCase {

	public void setUp() throws Exception {
		super.setUp();
		ResourceLocator.instance().register("client", "ant");
		reportFile = new InMemoryFileDecorator("report.xml");
	}

	public void tearDown() throws Exception {
		super.tearDown();
		ResourceLocator.instance().clear();
	}

	public void testShouldReturnReportLocationConstant() {
		assertEquals("IN MEMORY", reportFile.getParentAbsolutePath());
	}

}
