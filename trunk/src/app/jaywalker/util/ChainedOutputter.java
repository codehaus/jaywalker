package jaywalker.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
		try {
			File report = (File) locator.lookup("report.xml");
			InputStream is = new FileInputStream(report);
			for (int i = 0; i < outputters.length; i++) {
				if (i + 1 < outputters.length) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					outputters[i].transform(is, baos);
					is = new ByteArrayInputStream(baos.toByteArray());
				} else {
					outputters[i].transform(is, outputStream);
				}
			}
		} catch (IOException e) {
			throw new OutputterException("IOException while writing report.", e);
		}
	}

	public void transform(InputStream is, OutputStream os) {
		// TODO Auto-generated method stub
	}

}
