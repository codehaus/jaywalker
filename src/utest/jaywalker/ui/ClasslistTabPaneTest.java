package jaywalker.ui;

import junit.framework.TestCase;

public class ClasslistTabPaneTest extends TestCase {

	public void testShouldCreateStringArrayForClasslist() {
		assertLength(null, 0);
		assertLength("    ", 0);
		assertLength("test", 1);
		assertLength("test;test", 2);
		assertLength(";", 0);
		assertLength(";;", 0);
		assertLength("test;;", 1);
		assertLength("test;test;", 2);
	}

	private void assertLength(String input, int expectedLength) {
		ClasslistTabPane NULL_CLASS_LIST_TAB_PANE = new ClasslistTabPane(null,
				null, null);
		String[][] actual = NULL_CLASS_LIST_TAB_PANE.toStringArrayArray(input);
		assertEquals(expectedLength, actual.length);
	}
}
