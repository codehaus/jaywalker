package jaywalker.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class WriterOutputStream extends OutputStream {

	private final Writer writer;

	public WriterOutputStream(Writer writer) {
		this.writer = writer;
	}

	public void write(int arg0) throws IOException {
		writer.write(arg0);
	}

}
