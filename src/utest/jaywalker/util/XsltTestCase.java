package jaywalker.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import jaywalker.report.InMemoryReportFile;
import jaywalker.report.ReportFile;
import junit.framework.TestCase;

public class XsltTestCase extends TestCase {

	protected void assertOutputEquals(String xsltFilename, String input,
			String expected) throws IOException {
		ReportFile reportFile = new InMemoryReportFile();
		Writer writer = reportFile.getWriter();
		writer.write(input);
		writer.flush();
		writer.close();
		ResourceLocator.instance().register("report.xml", reportFile);
		XsltTransformer transformer = new XsltTransformer(xsltFilename);
		StringWriter stringWriter = new StringWriter();
		OutputStream outputStream = new WriterOutputStream(stringWriter);
		transformer.write(outputStream);
		String actual = stringWriter.getBuffer().toString();
		outputBytes(expected);
		outputBytes(actual);
		assertEquals(expected, actual);
	}

	private void outputBytes(String value) {
		byte[] bytes = value.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			char ch = (bytes[i] == 10 || bytes[i] == 13) ? ' ' : value
					.charAt(i);
			System.out.print(ch + "(" + bytes[i] + ")");
		}
		System.out.println();
	}

}
