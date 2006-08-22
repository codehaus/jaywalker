package jaywalker.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TabPage extends HtmlOutputter {

	private final String name;

	private final List list = new ArrayList();

	public TabPage(String name) {
		this.name = name;
	}

	public TabPage(String name, byte[] content) {
		this.name = name;
		add(content);
	}

	public void add(byte[] content) {
		list.add(content);
	}

	public void write(OutputStream os, String tabPaneName, String variable)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		h2(baos, "tab", name);
		final String id = toId(tabPaneName);
		javascript(baos, new String[] { addTabPage(variable, id) });
		for (int i = 0; i < list.size(); i++) {
			baos.write((byte[]) list.get(i));
		}
		baos.flush();
		baos.close();
		div(os, "tab-page", id, baos.toByteArray());
	}

	protected String addTabPage(String tabPane, String id) {
		return tabPane + ".addTabPage( document.getElementById( \"" + id
				+ "\" ) );";
	}

	private String toId(String tabPane) {
		return tabPane.toLowerCase() + "-" + name.toLowerCase() + "-page";
	}

}
