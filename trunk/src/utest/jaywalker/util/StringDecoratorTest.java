package jaywalker.util;

import junit.framework.TestCase;

public class StringDecoratorTest extends TestCase {

	public void testShouldSpaceAndReplaceWithNoTokens() {
		String input = "123 45";
		String expected = "  123 45\n";
		assertEquals(expected, new StringDecorator(input).spaceAndReplace(2,
				",", "\n"));
	}

	public void testShouldSpaceAndReplaceWithFiveTokens() {
		String input = "1,2,3,4,5";
		String expected = "  1\n  2\n  3\n  4\n  5\n";
		assertEquals(expected, new StringDecorator(input).spaceAndReplace(2,
				",", "\n"));
	}

}
