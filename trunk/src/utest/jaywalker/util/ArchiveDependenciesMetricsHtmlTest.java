package jaywalker.util;

import java.io.IOException;

public class ArchiveDependenciesMetricsHtmlTest extends HtmlTestCase {

	private static final String TITLE = "<h3>Archive Dependencies Metrics</h3>\r\n";

	private static final String TABLE_HEADER = "<tr>\r\n"
			+ "<th>Archive</th><th>Total Classes</th><th>Abstract Classes</th>"
			+ "<th>Abstractness</th>" + "<th>Afferent</th>"
			+ "<th>Efferent</th>" + "<th>Instability</th>"
			+ "<th>Distance</th>" + "\r\n</tr>\r\n";

	private static final String TABLE_START = TITLE + TAG_START_TABLE_DETAILS
			+ TABLE_HEADER;

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

	public void testShouldCreateTableHeaderForNoData() throws IOException {
		String input = "<?xml version=\"1.0\"?>\n<report>\n</report>";
		String expected = TABLE_START + "</table>\r\n";
		assertOutputEquals("archive-dependencies-metrics-html.xslt", input,
				expected);
	}

	public void testShouldCreateTableWithTotalClassAndAbstractClassCountForOneArchive()
			throws IOException {
		String input = "<?xml version=\"1.0\"?><report>" + SIMPLE_ARCHIVE
				+ "</report>";
		String expected = TABLE_START
				+ "<tr>\r\n<td>archive</td><td>9</td><td>6</td><td>0.67</td><td>0</td><td>1</td><td>1</td><td>1.67</td>"
				+ "\r\n</tr>\r\n" + "</table>\r\n";
		assertOutputEquals("archive-dependencies-metrics-html.xslt", input,
				expected);
	}

	public void testShouldCreateTableWithTotalClassAndAbstractClassCountForTwoArchive()
			throws IOException {
		String input = "<?xml version=\"1.0\"?><report>" + SIMPLE_ARCHIVE
				+ SIMPLE_ARCHIVE + "</report>";
		String expected = TABLE_START
				+ "<tr>\r\n<td>archive</td><td>9</td><td>6</td><td>0.67</td><td>0</td><td>1</td><td>1</td><td>1.67</td>\r\n</tr>\r\n"
				+ "<tr>\r\n<td>archive</td><td>9</td><td>6</td><td>0.67</td><td>0</td><td>1</td><td>1</td><td>1.67</td>\r\n</tr>\r\n"
				+ "</table>\r\n";
		assertOutputEquals("archive-dependencies-metrics-html.xslt", input,
				expected);
	}

	public void testShouldCreateTableWithTotalClassAndAbstractClassCountForNestedArchive()
			throws IOException {
		String input = "<?xml version=\"1.0\"?><report>" + NESTED_ARCHIVE
				+ "</report>";
		String expected = TABLE_START
				+ "<tr>\r\n<td>archive</td><td>9</td><td>6</td><td>0.67</td><td>0</td><td>5</td><td>1</td><td>1.67</td>\r\n</tr>\r\n"
				+ "<tr>\r\n<td>archive2</td><td>2</td><td>2</td><td>1</td><td>1</td><td>0</td><td>0</td><td>1</td>\r\n</tr>\r\n"
				+ "<tr>\r\n<td>archive3</td><td>1</td><td>0</td><td>0</td><td>1</td><td>0</td><td>0</td><td>0</td>\r\n</tr>\r\n"
				+ "</table>\r\n";
		assertOutputEquals("archive-dependencies-metrics-html.xslt", input,
				expected);
	}

}
