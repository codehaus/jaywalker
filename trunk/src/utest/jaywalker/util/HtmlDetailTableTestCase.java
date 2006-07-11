package jaywalker.util;

import java.io.IOException;

public abstract class HtmlDetailTableTestCase extends XsltTestCase {

	protected static final String TAG_START_TABLE_DETAILS = "<table class=\"details\" border=\"0\" cellpadding=\"5\" cellspacing=\"2\" width=\"95%\">\r\n";

	protected final String tableStart = toTitle(getTitleValue())
			+ TAG_START_TABLE_DETAILS + toTableHeader(getHeaderValues());

	private final String rowNoData = toTableDataRow(getNoDataRowValue(),
			getHeaderValues().length);

	public abstract String getXsltFileName();

	public abstract String getTitleValue();

	public abstract String[] getHeaderValues();

	public abstract String getNoDataRowValue();

	public String toTitle(String value) {
		return "<h3>" + value + "</h3>\r\n";
	}

	public String toTableHeader(String[] values) {
		StringBuffer sb = new StringBuffer("<tr>\r\n");
		for (int i = 0; i < values.length; i++) {
			sb.append("<th>").append(values[i]).append("</th>");
		}
		sb.append("\r\n</tr>\r\n");
		return sb.toString();
	}

	public String toTableDataRow(String value, int columnCnt) {
		StringBuffer sb = new StringBuffer("<tr>\r\n<td colspan=\"");
		sb.append(columnCnt).append("\"><i>");
		sb.append(value);
		sb.append("</i></td>\r\n</tr>\r\n");
		return sb.toString();
	}

	public void testShouldCreateTableHeaderForNoData() throws IOException {
		String input = "<?xml version=\"1.0\"?>\n<report>\n</report>";
		String expected = tableStart + rowNoData + "</table>\r\n";
		assertOutputEquals(getXsltFileName(), input, expected);
	}

}
