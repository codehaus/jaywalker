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

	public String getHref() {
		return "xmlns:jw=\"http://jaywalker.codehaus.org\"";
	}

	public String[] getHeaderValues() {
		return new MetricsHeaderBuilder("Package").build();
	}

	public String getNoDataRowValue() {
		return "No Packages Found";
	}

	public String getXsltFileName() {
		return "package-dependencies-metrics-html.xslt";
	}

	public String getTableId() {
		return "package-dependencies-metrics-table";
	}

	public void testShouldCreateTableWithTotalClassAndAbstractClassCountForOneArchive()
			throws IOException {
		String input = "<?xml version=\"1.0\"?><report>" + SIMPLE_ARCHIVE
				+ "</report>";
		String expected = tableStart
				+ "\n"
				+ "<tr class=\"odd\"><td></td><td>7</td><td>4</td><td>0.57</td><td>0</td><td>1</td><td>1</td><td>1.57</td>"
				+ "</tr>\n" + "</tbody>" + EOL + "</table>" + EOL;
		assertOutputEquals("package-dependencies-metrics-html.xslt", input,
				expected);
	}

}
