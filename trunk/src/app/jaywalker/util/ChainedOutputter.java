package jaywalker.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import jaywalker.report.ReportFile;

public class ChainedOutputter implements Outputter {

	private final ResourceLocator locator = ResourceLocator.instance();

	private Outputter[] outputters;

	public ChainedOutputter(Outputter[] outputters) {
		if (outputters == null) {
			this.outputters = new Outputter[0];
		} else {
			this.outputters = outputters;
		}
	}

	public void write(OutputStream outputStream) {
		ReportFile reportFile = (ReportFile) locator.lookup("report.xml");
		Reader reader = reportFile.getReader();
		InputStream is = new ReaderInputStream(reader);
		for (int i = 0; i < outputters.length; i++) {
			if (i + 1 < outputters.length) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				outputters[i].transform(is, baos);
				is = new ByteArrayInputStream(baos.toByteArray());
			} else {
				outputters[i].transform(is, outputStream);
			}
		}
	}

	public void transform(InputStream is, OutputStream os) {
		// TODO Auto-generated method stub
	}

}
