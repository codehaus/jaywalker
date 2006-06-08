package jaywalker.report;

import java.io.File;

import jaywalker.util.ResourceLocator;

public class ReportFileFactory {

	public ReportFile create() {
		String value = System.getProperty("ReportFile");
		if (InMemoryReportFile.class.getName().equals(value)) {
			return new InMemoryReportFile();
		}
		File outDir = (File) ResourceLocator.instance().lookup("outDir");
		return new DefaultReportFile(new File(outDir, "report.xml"));
	}

}
