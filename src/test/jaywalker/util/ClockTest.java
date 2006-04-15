package jaywalker.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class ClockTest extends TestCase {

	public void testShouldHaveIncreasingTimeOnStartCheck()
			throws InterruptedException {
		Clock clock = new Clock();

		clock.start("test");
		long firstCall = clock.check("test");
		doSomethingThatTakesTime();
		long secondCall = clock.check("test");
		assertTrue("firstCall: " + firstCall + ", secondCall: " + secondCall,
				firstCall < secondCall);

	}

	public void testShouldReturnDifferenceOnStartStopCheck()
			throws InterruptedException {
		Clock clock = new Clock();

		clock.start("test");
		doSomethingThatTakesTime();
		clock.stop("test");

		long firstCall = clock.check("test");
		doSomethingThatTakesTime();
		long secondCall = clock.check("test");
		assertEquals(firstCall, secondCall);
	}

	public void testShouldReturnZeroOnStopCheck() {
		Clock clock = new Clock();
		clock.stop("test");
		assertEquals(0, clock.check("test"));
	}

	public void testShouldReturnZeroOnCheck() {
		assertEquals(0, new Clock().check("test"));
	}

	private synchronized void doSomethingThatTakesTime()
			throws InterruptedException {
		wait(1);
	}

	private void assertToStringEquals(String patternString, final String value,
			final int time) {
		Pattern pattern = Pattern.compile(patternString);
		String actual = new Clock().toString(value, time);
		Matcher matcher = pattern.matcher(actual);
		assertTrue("pattern: \"" + patternString + "\", actual: \"" + actual
				+ "\"", matcher.matches());
	}

	public void testShouldReturnPrettyMilliseconds() {
		assertToStringEquals("test: \\d{0,3}ms", "test", 100);
	}

	public void testShouldReturnPrettySeconds() {
		assertToStringEquals("test: \\d{0,2}s \\d{0,3}ms", "test", 1050);
	}

	public void testShouldReturnPrettyMinutes() {
		assertToStringEquals("test: \\d{0,2}m \\d{0,2}s \\d{0,3}ms", "test",
				105000);
	}

	public void testShouldReturnPrettyHours() {
		assertToStringEquals("test: \\d{0,2}h \\d{0,2}m \\d{0,2}s \\d{0,3}ms",
				"test", 10500000);
	}

	public void testShouldReturnPrettyDays() {
		assertToStringEquals(
				"test: \\d+d \\d{0,2}h \\d{0,2}m \\d{0,2}s \\d{0,3}ms", "test",
				1050000000);
	}

	public void testShouldTimeASubject() {
		Clock clock = new Clock();
		ClockSubject subject = new ClockSubject() {
			public Object watch() {
				try {
					doSomethingThatTakesTime();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				return null;
			}
		};
		clock.watch("test", subject);
	}
}
