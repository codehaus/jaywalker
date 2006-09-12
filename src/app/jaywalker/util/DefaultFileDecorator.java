package jaywalker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;


public class DefaultFileDecorator implements FileDecorator {

	private File file;

	public DefaultFileDecorator(File file) {
		this.file = file;
	}

	public Writer getWriter() {
		try {
			return new FileWriter(file);
		} catch (IOException e) {
			throw new RuntimeException(
					"IOException thrown while creating FileWriter.", e);
		}
	}

	public Reader getReader() {
		try {
			return new FileReader(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"FileNotFoundException thrown while creating FileReader.",
					e);
		}
	}

	public String getParentAbsolutePath() {
		return file.getParentFile().getAbsolutePath();
	}

}
