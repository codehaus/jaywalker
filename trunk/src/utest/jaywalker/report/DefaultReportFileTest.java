package jaywalker.report;

import java.io.File;

import jaywalker.testutil.Path;
import jaywalker.util.DefaultFileDecorator;
import jaywalker.util.FileDecoratorTestCase;

public class DefaultReportFileTest extends FileDecoratorTestCase {

	private File file;

	public void setUp() throws Exception {
		super.setUp();
		file = new File(Path.DIR_TEMP, "report.xml");
		if (file.exists()) {
			file.delete();
		}
		reportFile = new DefaultFileDecorator(file);
	}

	public void testShouldReturnReportLocation() {
		assertEquals(file.getParentFile().getAbsolutePath(), reportFile.getParentAbsolutePath());
	}

}
