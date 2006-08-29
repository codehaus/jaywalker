package jaywalker.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TabPane {

	private final String name;

	private final List tabPageList = new ArrayList();

	private static final HtmlOutputter OUTPUTTER_HTML = new HtmlOutputter();

	public TabPane(String name) {
		this.name = name;
	}

	public void add(TabPage tabPage) {
		tabPageList.add(tabPage);
	}

	public void write(OutputStream os) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String id = getId();
		OUTPUTTER_HTML.javascript(baos, new String[] { newTabPane(
				getVariable(), id) });
		for (int i = 0; i < tabPageList.size(); i++) {
			final TabPage tabPage = (TabPage) tabPageList.get(i);
			tabPage.write(baos, name, getVariable());
		}
		baos.flush();
		baos.close();
		OUTPUTTER_HTML.div(os, "tab-pane", id, baos.toByteArray());
	}

	protected String newTabPane(String tabPane, String id) {
		return "var " + tabPane
				+ " = new WebFXTabPane( document.getElementById( \"" + id
				+ "\" ), true );";
	}

	private String getVariable() {
		return name.toLowerCase() + "TabPane";
	}

	private String getId() {
		return name.toLowerCase() + "-pane";
	}

}
