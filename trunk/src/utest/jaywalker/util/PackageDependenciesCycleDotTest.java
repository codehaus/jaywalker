package jaywalker.util;

import java.io.IOException;

public class PackageDependenciesCycleDotTest extends XsltTestCase {

	public void testShouldCreateEmptyDigraph() throws IOException {
		String input = "<?xml version=\"1.0\"?>\n<report>\n</report>";
		String expected = "digraph G {\n}\r\n";
		assertOutputEquals("package-dependencies-cycle-dot.xslt", input,
				expected);
	}

	public void testShouldCreateCycle() throws IOException {
		String input = "<?xml version=\"1.0\"?><report>"
				+ "<container type=\"directory\" url=\"jar:file:/jar1.jar!/cycle1/\" value=\"cycle1\">"
				+ "<dependency type=\"cycle\" value=\"3\">"
				+ "<container type=\"package\" url=\"jar:file:/jar1.jar!/cycle1/\" value=\"cycle1\"/>"
				+ "<container type=\"package\" url=\"jar:file:/jar2.jar!/cycle3/\" value=\"cycle3\"/>"
				+ "<container type=\"package\" url=\"jar:file:/jar1.jar!/cycle2/\" value=\"cycle2\"/>"
				+ "</dependency>" + "</container>" + "</report>";
		String expected = "digraph G {\n"
				+ "    \"jar:file:/jar1.jar!/cycle1/\" -> \"jar:file:/jar2.jar!/cycle3/\";\n"
				+ "    \"jar:file:/jar2.jar!/cycle3/\" -> \"jar:file:/jar1.jar!/cycle2/\";\n"
				+ "    \"jar:file:/jar1.jar!/cycle2/\" -> \"jar:file:/jar1.jar!/cycle1/\";\n"
				+ "}";
		assertOutputEquals("package-dependencies-cycle-dot.xslt", input,
				expected);
	}

}
