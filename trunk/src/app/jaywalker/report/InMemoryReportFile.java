package jaywalker.report;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import jaywalker.util.ResourceLocator;

public class InMemoryReportFile implements ReportFile {

	private StringBuffer sb;

	public InMemoryReportFile() {
		String client = (String) ResourceLocator.instance().lookup("client");
		ResourceLocator.instance().register(client + "ReportFile", this);
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
