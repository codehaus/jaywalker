package jaywalker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import jaywalker.testutil.Path;
import jaywalker.util.ZipInputStreamVisitor.ZipEntryListener;
import junit.framework.TestCase;

public class ZipExpanderTest extends TestCase {

	private ZipInputStream zis;

	public void setUp() throws Exception {
		super.setUp();
		zis = new ZipInputStream(new FileInputStream(Path.FILE_REPORT_ZIP));
	}

	public void testShouldValidateDestination() throws ZipException,
			IOException {
		assertExceptionThrown(ZipExpanderException.class,
				"Destination must be specified.", new ZipExpander(zis), null);
		assertExceptionThrown(ZipExpanderException.class,
				"Destination must be a directory.", new ZipExpander(zis),
				Path.FILE_REPORT_ZIP);
	}

	public void testShouldThrowIOExceptionWhileVisitingZipFile()
			throws ZipException, IOException {
		ZipExpander expander = new ZipExpander(zis) {
			protected ZipEntryListener createZipEntryListener(File dest) {
				return new ZipEntryListener() {
					public void visit(ZipInputStream zis, ZipEntry zipEntry)
							throws IOException {
						throw new IOException("test exception");
					}
				};
			}
		};
		assertExceptionThrown(IOException.class,
				"IOException thrown while visiting ZipFile.", expander,
				Path.DIR_REPORT_ZIP);

	}

	public void testShouldExpandReportZipToTempDirectory() throws ZipException,
			IOException {
		ZipExpander expander = new ZipExpander(zis);
		expander.expand(Path.DIR_REPORT_ZIP);
	}

	private void assertExceptionThrown(Class expected, String message,
			ZipExpander expander, File dest) throws ZipException, IOException {
		try {
			expander.expand(dest);
			fail(expected + " should have been thrown.");
		} catch (ZipExpanderException e) {
			assertEquals(message, e.getMessage());
		}
	}

}
