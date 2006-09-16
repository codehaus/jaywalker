package jaywalker.html;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import jaywalker.testutil.Path;
import jaywalker.util.FileDecorator;
import jaywalker.util.InMemoryFileDecorator;
import jaywalker.xml.ConfigVisitor;
import junit.framework.TestCase;

public class HtmlConfigVisitorTest extends TestCase {
	public void testShouldThrowRuntimeExceptionAfterVisitingTabPaneDecorator()
			throws FileNotFoundException {
		final IOException expected = new IOException("Expected");
		OutputStream os = new ByteArrayOutputStream();
		FileDecorator reportFile = new InMemoryFileDecorator();
		ConfigVisitor visitor = new HtmlConfigVisitor(os, reportFile) {
			public void visit(TabPaneDecorator tabPaneDecorator)
					throws IOException {
				throw expected;
			}
		};
		assertSameException(visitor, expected);
	}

	private void assertSameException(ConfigVisitor visitor,
			final Exception expected) throws FileNotFoundException {
		try {
			visitor.setConfig(new FileInputStream(Path.FILE_REPORT_CONFIG_XML));
			visitor.accept();
			fail("RuntimeException should have been thrown.");
		} catch (RuntimeException e) {
			assertSame(expected, e.getCause());
		}
	}

}
