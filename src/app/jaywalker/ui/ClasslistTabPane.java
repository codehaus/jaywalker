package jaywalker.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ClasslistTabPane implements Content {

	private TabPane mainTabPane = new TabPane("Main");

	private TabCreator tabCreator = new TabCreator(mainTabPane);

	private final String deepValue;

	private final String shallowValue;

	private final String systemValue;

	private static final HtmlOutputter OUTPUTTER_HTML = new HtmlOutputter();

	public ClasslistTabPane(String deepValue, String shallowValue,
			String systemValue) {
		this.deepValue = deepValue;
		this.shallowValue = shallowValue;
		this.systemValue = systemValue;
	}

	public byte[] getBytes() throws IOException {
		addPageToMainPane("Deep", deepValue);
		addPageToMainPane("Shallow", shallowValue);
		addPageToMainPane("System", systemValue);
		return tabCreator.getBytes();
	}

	private void addPageToMainPane(String classlistType, String classlist)
			throws IOException {
		String[][] toStringArrayArray = toStringArrayArray(classlistType,
				classlist);
		mainTabPane.add(new TabPage(classlistType, createTable("classlist-"
				+ classlistType.toLowerCase() + "-table", "sort-table",
				toStringArrayArray)));
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
