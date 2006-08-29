package jaywalker.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TabPage {

	private final String name;

	private final List list = new ArrayList();

	private static final HtmlOutputter OUTPUTTER_HTML = new HtmlOutputter();

	private final String title;

	private final String description;

	public TabPage(String name, String title, String description) {
		this.name = name;
		this.title = title;
		this.description = description;
	}

	public TabPage(String name, String title, String description, byte[] content) {
		this.name = name;
		this.title = title;
		this.description = description;
		add(content);
	}

	public void add(byte[] content) {
		list.add(content);
	}

	private byte[] toolTip(String value, String tipTitle, String tipValue)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OUTPUTTER_HTML.toolTip(baos, value, tipTitle, tipValue);
		baos.flush();
		baos.close();
		return baos.toByteArray();
	}

	public void write(OutputStream os, String tabPaneName, String variable)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OUTPUTTER_HTML.h2(baos, "tab", new String(toolTip(name, title,
				description)));
		final String id = toId(tabPaneName);
		OUTPUTTER_HTML.javascript(baos,
				new String[] { addTabPage(variable, id) });
		for (int i = 0; i < list.size(); i++) {
			baos.write((byte[]) list.get(i));
		}
		baos.flush();
		baos.close();
		OUTPUTTER_HTML.div(os, "tab-page", id, baos.toByteArray());
	}

	protected String addTabPage(String tabPane, String id) {
		return tabPane + ".addTabPage( document.getElementById( \"" + id
				+ "\" ) );";
	}

	private String toId(String tabPane) {
		return tabPane.toLowerCase() + "-" + name.toLowerCase() + "-page";
	}

}
