package jaywalker.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DotOutputter implements Outputter {

	private final ResourceLocator locator = ResourceLocator.instance();

	private final String filename;

	public DotOutputter(String filename) {
		this.filename = filename;
	}

	public void transform(FileDecorator file, OutputStream outputStream) {
	}

	public void transform(InputStream is, OutputStream os) {
		File parentFile = (File) locator.lookup("outDir");
		File file = new File(parentFile, filename);
		try {
			file.delete();
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			FileSystem.readInputStreamIntoOutputStream(is, fos);
			new DotProcess().executeDot(file);
			os.write(createImageTag(file));
		} catch (IOException e) {
			throw new OutputterException(
					"IOException thrown while creating DOT file", e);
		}
	}

	private byte[] createImageTag(File file) {
		String tag = "<img src=\"" + DotProcess.toPngFilename(file).getName()
				+ "\"/>";
		return tag.getBytes();
	}

}
