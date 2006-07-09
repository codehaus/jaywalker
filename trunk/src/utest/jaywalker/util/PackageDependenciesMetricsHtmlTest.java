package jaywalker.util;

import java.io.IOException;

public class PackageDependenciesMetricsHtmlTest extends HtmlTestCase {
	private static final String TITLE = "<h3>Package Dependencies Metrics</h3>\r\n";

	private static final String TABLE_HEADER = "<tr>\r\n"
			+ "<th>Package</th><th>Total Classes</th><th>Abstract Classes</th>"
			+ "<th>Abstractness</th>" + "<th>Afferent</th>"
			+ "<th>Efferent</th>" + "<th>Instability</th>"
			+ "<th>Distance</th>" + "\r\n</tr>\r\n";

	private static final String TABLE_START = TITLE + TAG_START_TABLE_DETAILS
			+ TABLE_HEADER;

	private static final String SIMPLE_ARCHIVE = "<container type=\"archive\" url=\"archive\" value=\"\">"
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
			+ "<container type=\"directory\" url=\"dir\" value=\"dir2\">"
			+ "<element type=\"should not see\" url=\"url\"/>"
			+ "</container>"
			+ "<dependency type=\"resolved\" value=\"1\">"
			+ "<container type=\"package\" value=\"dir\"/>"
			+ "<element type=\"class\" url=\"should-not-see\"/>"
			+ "</dependency>"
			+ "<element type=\"abstract\" url=\"url\"/>"
			+ "</container>";

	public void testShouldCreateTableHeaderForNoData() throws IOException {
		String input = "<?xml version=\"1.0\"?>\n<report>\n</report>";
		String expected = TABLE_START + "</table>\r\n";
		assertOutputEquals("package-dependencies-metrics-html.xslt", input,
				expected);
	}

	public void testShouldCreateTableWithTotalClassAndAbstractClassCountForOneArchive()
			throws IOException {
		String input = "<?xml version=\"1.0\"?><report>" + SIMPLE_ARCHIVE
				+ "</report>";
		String expected = TABLE_START
				+ "<tr>\r\n<td></td><td>7</td><td>4</td><td>0.57</td><td>0</td><td>1</td><td>1</td><td>1.57</td>"
				+ "\r\n</tr>\r\n"
				+ "<tr>\r\n<td>dir</td><td>2</td><td>2</td><td>1</td><td>1</td><td>0</td><td>0</td><td>1</td>"
				+ "\r\n</tr>\r\n" + "</table>\r\n";
		assertOutputEquals("package-dependencies-metrics-html.xslt", input,
				expected);
	}

}
