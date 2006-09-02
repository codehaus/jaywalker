package jaywalker.report;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import jaywalker.util.ResourceLocator;

public class InMemoryReportFile implements ReportFile {

	private StringBuffer sb = new StringBuffer();

	public InMemoryReportFile() {
	}

	public InMemoryReportFile(String filename) {
		ResourceLocator locator = ResourceLocator.instance();
		String client = lookupClient(locator);
		locator.register(client + "ReportFile-" + filename, this);
	}

	private String lookupClient(ResourceLocator locator) {
		if (locator.contains("client")) {
			return (String) locator.lookup("client");
		} else {
			return "ant";
		}
	}

	public Writer getWriter() {
		StringWriter writer = new StringWriter();
		sb = writer.getBuffer();
		return writer;
	}

	public Reader getReader() {
		return new StringReader(sb.toString());
	}

	public String getParentAbsolutePath() {
		return "IN MEMORY";
	}

}
