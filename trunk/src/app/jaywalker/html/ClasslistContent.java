package jaywalker.html;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jaywalker.util.ResourceLocator;
import jaywalker.xml.bind.TabPage;

public class ClasslistContent implements Content {

	private static final HtmlOutputter OUTPUTTER_HTML = new HtmlOutputter();

	public byte[] create(TabPage tabPage) throws IOException {
		String classlistType = tabPage.getType();
		String classlist = (String) ResourceLocator.instance().lookup(
				"classlist-" + classlistType + "-value");
		String[][] toStringArrayArray = toStringArrayArray(classlistType,
				classlist);

		return createTable("classlist-" + classlistType.toLowerCase()
				+ "-table", "sort-table", toStringArrayArray);
	}

	private byte[] createTable(String id, String clazz, String[][] values)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OUTPUTTER_HTML.table(baos, id, clazz,
				new String[] { "Classlist Element" }, values);
		baos.flush();
		baos.close();
		return baos.toByteArray();
	}

	private String[][] toStringArrayArray(String classlistType, String value) {
		String[][] toStringArrayArray = toStringArrayArray(value);
		if (toStringArrayArray.length == 0) {
			toStringArrayArray = new String[][] { { "<i>No " + classlistType
					+ " Classlist Given</i>" } };
		}
		return toStringArrayArray;
	}

	protected String[][] toStringArrayArray(String classlist) {
		String input = format(classlist);
		if (isEmpty(input)) {
			return new String[0][0];
		}
		String[] values = input.split(";");
		String[][] output = new String[values.length][1];
		for (int i = 0; i < values.length; i++) {
			output[i][0] = values[i];
		}
		return output;
	}

	private boolean isEmpty(String input) {
		return input == null || input.length() == 0;
	}

	private String format(String value) {
		return (value == null) ? null : value.trim();
	}

}