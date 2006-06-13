package jaywalker.util;

import junit.framework.TestCase;

public class DotOutputterFactoryTest extends TestCase {

	public void testShouldCreateDotOutputter() {
		Outputter outputter = new DotOutputterFactory().create("archive.resolved.dot");
		assertEquals(DotOutputter.class, outputter.getClass());
	}
	
	public void testShouldCreateStubOutputter() {
		System.setProperty("DotOutputter", "jaywalker.util.StubOutputter");
		Outputter outputter = new DotOutputterFactory().create("archive.resolved.dot");
		assertEquals(StubOutputter.class, outputter.getClass());
	}
}
