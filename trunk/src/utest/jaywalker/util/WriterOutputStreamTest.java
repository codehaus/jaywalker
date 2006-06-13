package jaywalker.util;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

public class WriterOutputStreamTest extends TestCase {

	public void testShouldConvertWriterToOutputStream() throws IOException {
		String expected = "this is a string that is interesting.";
		StringWriter writer = new StringWriter();
		WriterOutputStream wos = new WriterOutputStream(writer);
		byte[] bytes = expected.getBytes();
		wos.write(bytes);
		wos.flush();
		wos.close();
		assertEquals(expected, writer.getBuffer().toString());
	}

}
