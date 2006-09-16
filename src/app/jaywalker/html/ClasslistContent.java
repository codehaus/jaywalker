package jaywalker.html;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jaywalker.util.ResourceLocator;
import jaywalker.util.StringDecorator;
import jaywalker.xml.bind.TabPage;

public class ClasslistContent implements Content {

	private static final HtmlBuilder OUTPUTTER_HTML = new HtmlBuilder();

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
			toStringArrayArray = new String[][] { { "<i>No "
					+ new StringDecorator(classlistType).capitalize() + " Classlist Given</i>" } };
		}
		return toStringArrayArray;
	}

	protected String[][] toStringArrayArray(String classlist) {
		String input = new StringDecorator(classlist).trim();
		if (new StringDecorator(input).isEmpty()) {
			return new String[0][0];
		}
		String[] values = input.split(";");
		String[][] output = new String[values.length][1];
		for (int i = 0; i < values.length; i++) {
			output[i][0] = values[i];
		}
		return output;
	}

}
