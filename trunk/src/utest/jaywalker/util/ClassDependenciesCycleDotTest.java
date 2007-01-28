package jaywalker.util;

import java.io.IOException;

public class ClassDependenciesCycleDotTest extends XsltTestCase {

	public void testShouldCreateEmptyDigraph() throws IOException {
		String input = "<?xml version=\"1.0\"?>\n<report>\n</report>";
		String expected = "digraph G {\n}" + EOL;
		assertOutputEquals("class-dependencies-cycle-dot.xslt", input, expected);
	}

	public void testShouldCreateCycle() throws IOException {
		String input = "<?xml version=\"1.0\"?><report>"
				+ "<element type=\"class\" url=\"jar:file:/jar1.jar!/cycle1/Node1.class\" value=\"cycle1.Node1\">"
				+ "<dependency type=\"cycle\" value=\"3\">"
				+ "<element type=\"class\" url=\"jar:file:/jar1.jar!/cycle1/Node1.class\" value=\"cycle1.Node1\"/>"
				+ "<element type=\"class\" url=\"jar:file:/jar2.jar!/cycle3/Node3.class\" value=\"cycle3.Node3\"/>"
				+ "<element type=\"class\" url=\"jar:file:/jar1.jar!/cycle2/Node2.class\" value=\"cycle2.Node2\"/>"
				+ "</dependency>" + "</element>" + "</report>";
		String expected = "digraph G {\n"
				+ "    \"jar:file:/jar1.jar!/cycle1/Node1.class\" -> \"jar:file:/jar2.jar!/cycle3/Node3.class\";\n"
				+ "    \"jar:file:/jar2.jar!/cycle3/Node3.class\" -> \"jar:file:/jar1.jar!/cycle2/Node2.class\";\n"
				+ "    \"jar:file:/jar1.jar!/cycle2/Node2.class\" -> \"jar:file:/jar1.jar!/cycle1/Node1.class\";\n"
				+ "}";
		assertOutputEquals("class-dependencies-cycle-dot.xslt", input, expected);
	}
}
