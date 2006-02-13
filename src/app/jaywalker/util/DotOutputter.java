package jaywalker.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class DotOutputter implements Outputter {

	private final ResourceLocator locator = ResourceLocator.instance();

	private final String filename;

	public DotOutputter(String filename) {
		this.filename = filename;
	}

	public void write(OutputStream outputStream) {

	}

	public String transform(String value) {
		File parentFile = ((File) locator.lookup("report.xml")).getParentFile();
		File file = new File(parentFile, filename);
		try {
			file.delete();
			file.createNewFile();
			FileSystem.writeStringIntoFile(file, value);
			Shell.executeDot(file);
		} catch (IOException e) {
			// do nothing
		}

		return "<img src=\"" + Shell.toPngFilename(file).getName() + "\"/>";
	}

}
