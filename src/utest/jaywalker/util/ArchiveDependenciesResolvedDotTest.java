package jaywalker.util;

import java.io.IOException;

public class ArchiveDependenciesResolvedDotTest extends DotTestCase {

	public void testShouldCreateEmptyDigraph() throws IOException {
		String input = "<?xml version=\"1.0\"?>\n<report>\n</report>";
		String expected = "digraph G {\n}\r\n";
		assertOutputEquals("archive-dependencies-resolved-dot.xslt", input,
				expected);
	}

	public void testShouldCreateParentChildComposition() throws IOException {
		String input = "<?xml version=\"1.0\"?>\n<report>\n"
				+ "<container type=\"archive\" url=\"parent\">"
				+ "<container type=\"archive\" url=\"child\"/>"
				+ "</container>" + "</report>";
		String expected = "digraph G {\n    \"parent\" -> \"child\" "
				+ "[style=dotted,arrowtail=diamond];\n" + "}";
		assertOutputEquals("archive-dependencies-resolved-dot.xslt", input,
				expected);
	}

	public void testShouldCreateParentChildGrandChildComposition()
			throws IOException {
		String input = "<?xml version=\"1.0\"?>"
				+ "<report>"
				+ "<container type=\"archive\" url=\"parent1\">"
				+ "<dependency type=\"resolved\" value=\"2\">"
				+ "<container type=\"archive\" url=\"should not be included\" value=\"no-op\"/>"
				+ "<container type=\"archive\" url=\"also should not be included\" value=\"no-op\"/>"
				+ "</dependency>"
				+ "<container type=\"archive\" url=\"child1\">"
				+ "<container type=\"directory\" url=\"no-op1\">"
				+ "<container type=\"archive\" url=\"grandchild1\"/>"
				+ "</container>" + "</container>" + "</container>"
				+ "<container type=\"archive\" url=\"parent2\">"
				+ "<container type=\"archive\" url=\"child2\">"
				+ "<container type=\"directory\" url=\"no-op2\">"
				+ "<container type=\"archive\" url=\"grandchild2\"/>"
				+ "</container>" + "</container>" + "</container>"
				+ "</report>";
		String expected = "digraph G {\n"
				+ "    \"parent1\" -> \"should not be included\";\n"
				+ "    \"parent1\" -> \"also should not be included\";\n"
				+ "    \"parent1\" -> \"child1\" "
				+ "[style=dotted,arrowtail=diamond];\n"
				+ "    \"child1\" -> \"grandchild1\" "
				+ "[style=dotted,arrowtail=diamond];\n"
				+ "    \"parent2\" -> \"child2\" "
				+ "[style=dotted,arrowtail=diamond];\n"
				+ "    \"child2\" -> \"grandchild2\" "
				+ "[style=dotted,arrowtail=diamond];\n" + "}";
		assertOutputEquals("archive-dependencies-resolved-dot.xslt", input,
				expected);
	}
}
