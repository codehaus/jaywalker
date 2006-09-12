package jaywalker.html;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jaywalker.xml.bind.TabPane;

public class TabPaneDecorator {

	private static final HtmlBuilder OUTPUTTER_HTML = new HtmlBuilder();

	private final List tabPageDecoratorList = new ArrayList();

	private final TabPane tabPane;

	public TabPaneDecorator(TabPane tabPane) {
		this.tabPane = tabPane;
	}

	public TabPane getTabPane() {
		return tabPane;
	}

	public void add(TabPageDecorator decorator) {
		tabPageDecoratorList.add(decorator);
	}

	public void html(OutputStream os) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String id = getId();
		OUTPUTTER_HTML.javascript(baos, new String[] { newTabPane(
				getVariable(), id) });
		for (int i = 0; i < tabPageDecoratorList.size(); i++) {
			TabPageDecorator tabPageDecorator = (TabPageDecorator) tabPageDecoratorList
					.get(i);
			tabPageDecorator.html(baos, tabPane.getType(), getVariable());
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
		return tabPane.getType().toLowerCase() + "TabPane";
	}

	private String getId() {
		return tabPane.getType().toLowerCase() + "-pane";
	}

}
