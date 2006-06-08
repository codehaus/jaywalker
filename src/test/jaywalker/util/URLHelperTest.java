package jaywalker.util;

import junit.framework.TestCase;

public class URLHelperTest extends TestCase {

	public void testShouldNotStripProtocolIfNotTopLevelArchive() {
		String input = "jar:something.jar!/something-else";
		assertEquals(input, new URLHelper()
				.stripProtocolIfTopLevelArchive(input));
	}
	
	public void testShouldStripProtocolIfTopLevelArchive() {
		String input = "jar:something.jar!/";
		assertEquals("something.jar", new URLHelper()
				.stripProtocolIfTopLevelArchive(input));
	}
	
	public void testShouldStripProtocolIfTopLevelArchive2() {
		String input = "jar:something.jar!/something-else.jar!/";
		assertEquals("jar:something.jar!/something-else.jar", new URLHelper()
				.stripProtocolIfTopLevelArchive(input));
	}
	
	public void testShouldStripProtocolIfTopLevelArchive3() {
		String input = "jar:something.jar";
		assertEquals(input, new URLHelper()
				.stripProtocolIfTopLevelArchive(input));
	}

	public void testShouldStripProtocolIfTopLevelArchive4() {
		String input = "onejar:jaywalker.classlist.ClassElement$Creator.class";
		assertEquals(input, new URLHelper()
				.stripProtocolIfTopLevelArchive(input));
	}
	
}
