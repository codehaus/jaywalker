package jaywalker.util;

import java.io.IOException;

public class PackageDependenciesMetricsHtmlTest extends HtmlDetailTableTestCase {

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

	public String getTitleValue() {
		return "Package Dependencies Metrics";
	}

	public String[] getHeaderValues() {
		return new String[] { "Package", "Total Classes", "Abstract Classes",
				"Abstractness", "Afferent", "Efferent", "Instability",
				"Distance" };
	}

	public String getNoDataRowValue() {
		return "No Packages Found";
	}

	public String getXsltFileName() {
		return "package-dependencies-metrics-html.xslt";
	}

	public void testShouldCreateTableWithTotalClassAndAbstractClassCountForOneArchive()
			throws IOException {
		String input = "<?xml version=\"1.0\"?><report>" + SIMPLE_ARCHIVE
				+ "</report>";
		String expected = tableStart
				+ "<tr>\r\n<td></td><td>7</td><td>4</td><td>0.57</td><td>0</td><td>1</td><td>1</td><td>1.57</td>"
				+ "\r\n</tr>\r\n"
				+ "<tr>\r\n<td>dir</td><td>2</td><td>2</td><td>1</td><td>1</td><td>0</td><td>0</td><td>1</td>"
				+ "\r\n</tr>\r\n" + "</table>\r\n";
		assertOutputEquals("package-dependencies-metrics-html.xslt", input,
				expected);
	}

}
