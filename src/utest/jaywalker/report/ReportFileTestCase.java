package jaywalker.report;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import junit.framework.TestCase;

public abstract class ReportFileTestCase extends TestCase {

	protected ReportFile reportFile;

	public void testShouldWriteAndReadUsingReportFile() throws IOException {
		String expected = "This is an interesting string";
		Writer writer = reportFile.getWriter();
		writer.write(expected);
		writer.close();
		Reader reader = reportFile.getReader();
		assertEquals(expected, toString(reader));
	}
	
	protected String toString(Reader reader) throws IOException {
		StringBuffer sb = new StringBuffer();
		int i = reader.read();
		while (i != -1) {
			sb.append((char) i);
			i = reader.read();
		}
		reader.close();
		return sb.toString();
	}


}
