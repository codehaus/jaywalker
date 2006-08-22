package jaywalker.util;

import java.io.IOException;

public class ArchiveDependenciesMetricsHtmlTest extends HtmlDetailTableTestCase {

	private static final String SIMPLE_ARCHIVE = "<container type=\"archive\" url=\"archive\">"
			+ "<element type=\"class\" url=\"url\"/>"
			+ "<element type=\"class\" url=\"url\"/>"
			+ "<element type=\"abstract\" url=\"url\"/>"
			+ "<element type=\"class\" url=\"url\"/>"
			+ "<element type=\"interface\" url=\"url\"/>"
			+ "<element type=\"interface\" url=\"url\"/>"
			+ "<container type=\"directory\" url=\"dir\" value=\"dir\">"
			+ "<element type=\"interface\" url=\"url\"/>"
			+ "<element type=\"interface\" url=\"url\"/>"
			+ "</container>"
			+ "<dependency type=\"resolved\" value=\"1\">"
			+ "<container type=\"archive\" url=\"should-not-see\"/>"
			+ "<element type=\"class\" url=\"should-not-see\"/>"
			+ "</dependency>"
			+ "<element type=\"abstract\" url=\"url\"/>"
			+ "</container>";

	private static final String NESTED_ARCHIVE = "<container type=\"archive\" url=\"archive\">"
			+ "<element type=\"class\" url=\"url\"/>"
			+ "<element type=\"class\" url=\"url\"/>"
			+ "<element type=\"abstract\" url=\"url\"/>"
			+ "<element type=\"class\" url=\"url\"/>"
			+ "<element type=\"interface\" url=\"url\"/>"
			+ "<element type=\"interface\" url=\"url\"/>"
			+ "<container type=\"directory\" url=\"dir\" value=\"dir\">"
			+ "<container type=\"archive\" url=\"archive2\">"
			+ "<element type=\"interface\" url=\"url\"/>"
			+ "<element type=\"interface\" url=\"url\"/>"
			+ "<container type=\"archive\" url=\"archive3\">"
			+ "<element type=\"class\" url=\"url\"/>"
			+ "</container>"
			+ "</container>"
			+ "<element type=\"interface\" url=\"url\"/>"
			+ "<element type=\"interface\" url=\"url\"/>"
			+ "</container>"
			+ "<dependency type=\"resolved\" value=\"5\">"
			+ "<container type=\"archive\" url=\"archive2\"/>"
			+ "<container type=\"archive\" url=\"archive3\"/>"
			+ "<container type=\"archive\" url=\"should-not-see\"/>"
			+ "<container type=\"archive\" url=\"should-not-see\"/>"
			+ "<container type=\"archive\" url=\"should-not-see\"/>"
			+ "</dependency>"
			+ "<element type=\"abstract\" url=\"url\"/>"
			+ "</container>";

	public String getTitleValue() {
		return "Archive Dependencies Metrics";
	}

	public String[] getHeaderValues() {
		return new String[] { "Archive", "Total Classes", "Abstract Classes",
				"Abstractness", "Afferent", "Efferent", "Instability",
				"Distance" };
	}

	public String getNoDataRowValue() {
		return "No Archives Found";
	}

	public String getXsltFileName() {
		return "archive-dependencies-metrics-html.xslt";
	}
	
	public String getTableId() {
		return "archive-dependencies-metrics-table";
	}

	public void testShouldCreateTableWithTotalClassAndAbstractClassCountForOneArchive()
			throws IOException {
		String input = "<?xml version=\"1.0\"?><report>" + SIMPLE_ARCHIVE
				+ "</report>";
		String expected = tableStart + "\n"
				+ "<tr class=\"odd\"><td>archive</td><td>9</td><td>6</td><td>0.67</td><td>0</td><td>1</td><td>1</td><td>1.67</td>"
				+ "</tr></tbody>\r\n</table>\r\n";
		assertOutputEquals("archive-dependencies-metrics-html.xslt", input,
				expected);
	}

	public void testShouldCreateTableWithTotalClassAndAbstractClassCountForTwoArchive()
			throws IOException {
		String input = "<?xml version=\"1.0\"?><report>" + SIMPLE_ARCHIVE
				+ SIMPLE_ARCHIVE + "</report>";
		String expected = tableStart + "\n"
				+ "<tr class=\"odd\"><td>archive</td><td>9</td><td>6</td><td>0.67</td><td>0</td><td>1</td><td>1</td><td>1.67</td></tr>\n"
				+ "<tr class=\"even\"><td>archive</td><td>9</td><td>6</td><td>0.67</td><td>0</td><td>1</td><td>1</td><td>1.67</td></tr>"
				+ "</tbody>\r\n"
				+ "</table>\r\n";
		assertOutputEquals("archive-dependencies-metrics-html.xslt", input,
				expected);
	}

	public void testShouldCreateTableWithTotalClassAndAbstractClassCountForNestedArchive()
			throws IOException {
		String input = "<?xml version=\"1.0\"?><report>" + NESTED_ARCHIVE
				+ "</report>";
		String expected = tableStart + "\n"
				+ "<tr class=\"odd\"><td>archive</td><td>9</td><td>6</td><td>0.67</td><td>0</td><td>5</td><td>1</td><td>1.67</td></tr>\n"
				+ "<tr class=\"even\"><td>archive2</td><td>2</td><td>2</td><td>1</td><td>1</td><td>0</td><td>0</td><td>1</td></tr>\n"
				+ "<tr class=\"odd\"><td>archive3</td><td>1</td><td>0</td><td>0</td><td>1</td><td>0</td><td>0</td><td>0</td></tr>"
				+ "</tbody>\r\n"
				+ "</table>\r\n";
		assertOutputEquals("archive-dependencies-metrics-html.xslt", input,
				expected);
	}

}
