package jaywalker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
		final File file = (File) locator.lookup("report.xml");
		try {
			String report = FileSystem.readFileIntoString(file);
			for (int i = 0; i < outputters.length; i++) {
				report = outputters[i].transform(report);
			}
			outputStream.write(report.getBytes());
		} catch (FileNotFoundException e) {
			throw new OutputterException("File not found : " + file, e);
		} catch (IOException e) {
			throw new OutputterException("IOException for : " + file, e);
		}
	}

	public String transform(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
