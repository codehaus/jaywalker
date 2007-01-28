package jaywalker.util;

import java.io.IOException;

public abstract class HtmlDetailTableTestCase extends XsltTestCase {

	protected static final String EOL = System.getProperty("line.separator");

	protected final String tagStartTableDetails = "<table "
			+ ((getHref().length() == 0) ? "" : getHref() + " ")
			+ "class=\"sort-table\" id=\"" + getTableId() + "\">" + EOL;

	protected final String tableStart = tagStartTableDetails
			+ toTableHeader(getHeaderValues());

	private final String rowNoData = toTableDataRow(getNoDataRowValue(),
			getHeaderValues().length);

	public String getHref() {
		return "";
	}

	public abstract String getXsltFileName();

	public abstract String getTitleValue();

	public abstract String[] getHeaderValues();

	public abstract String getNoDataRowValue();

	public abstract String getTableId();

	public String toTitle(String value) {
		return "<h3>" + value + "</h3>" + EOL;
	}

	public String toTableHeader(String[] values) {
		StringBuffer sb = new StringBuffer("<thead>" + EOL + "<tr>" + EOL);
		for (int i = 0; i < values.length; i++) {
			sb.append("<td>").append(values[i]).append("</td>");
		}
		sb.append(EOL + "</tr>" + EOL + "</thead>" + EOL + "<tbody>");
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
		String expected = tableStart + rowNoData + "</tbody>" + EOL
				+ "</table>" + EOL;
		assertOutputEquals(getXsltFileName(), input, expected);
	}

}
