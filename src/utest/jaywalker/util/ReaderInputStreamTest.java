package jaywalker.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

public class ReaderInputStreamTest extends TestCase {

	public void testShouldConvertReaderToInputStream() throws IOException {
		String expected = "this is a string that is interesting.";
		Reader reader = new StringReader(expected);
		ReaderInputStream ris = new ReaderInputStream(reader);
		assertEquals(expected, toString(ris));
	}

	private String toString(ReaderInputStream ris) throws IOException {
		StringBuffer sb = new StringBuffer();
		int i = ris.read();
		while (i != -1) {
			sb.append((char) i);
			i = ris.read();
		}
		ris.close();
		return sb.toString();
	}

}
