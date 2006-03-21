package jaywalker.util;

import junit.framework.TestCase;

public class SortedArrayTest extends TestCase {
	public void testShouldCreateInstanceWhichContainsExpected() {
		String[] data = { "10", "01", "05", "12" };
		String[] expected = { "01", "05", "10", "12" };
		SortedArray array = SortedArray.valueOf(data);
		for (int i = 0; i < expected.length; i++) {
			assertTrue(array.contains(expected[i]));
		}
	}
}
