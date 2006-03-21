package jaywalker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

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
			String report = (String) locator.lookup("report.xml.value");
			for (int i = 0; i < outputters.length; i++) {
				Date start = new Date();
				report = outputters[i].transform(report);
				System.out.println(new Date().getTime() - start.getTime());
			}
			outputStream.write(report.getBytes());
		} catch (IOException e) {
			throw new OutputterException("IOException while writing report.", e);
		}
	}

	public String transform(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
