package jaywalker.util;

import java.io.IOException;

public abstract class HtmlDetailTableTestCase extends XsltTestCase {

	protected final String tagStartTableDetails = "<table class=\"sort-table\" id=\""
			+ getTableId() + "\">\r\n";

	protected final String tableStart = tagStartTableDetails
			+ toTableHeader(getHeaderValues());

	private final String rowNoData = toTableDataRow(getNoDataRowValue(),
			getHeaderValues().length);

	public abstract String getXsltFileName();

	public abstract String getTitleValue();

	public abstract String[] getHeaderValues();

	public abstract String getNoDataRowValue();

	public abstract String getTableId();

	public String toTitle(String value) {
		return "<h3>" + value + "</h3>\r\n";
	}

	public String toTableHeader(String[] values) {
		StringBuffer sb = new StringBuffer("<thead>\r\n<tr>\r\n");
		for (int i = 0; i < values.length; i++) {
			sb.append("<td>").append(values[i]).append("</td>");
		}
		sb.append("\r\n</tr>\r\n</thead>\r\n<tbody>");
		return sb.toString();
	}

	public String toTableDataRow(String value, int columnCnt) {
		StringBuffer sb = new StringBuffer("<tr><td colspan=\"");
		sb.append(columnCnt).append("\"><i>");
		sb.append(value);
		sb.append("</i></td></tr>");
		return sb.toString();
	}

	public void testShouldCreateTableHeaderForNoData() throws IOException {
		String input = "<?xml version=\"1.0\"?>\n<report>\n</report>";
		String expected = tableStart + rowNoData + "</tbody>\r\n</table>\r\n";
		assertOutputEquals(getXsltFileName(), input, expected);
	}

}
