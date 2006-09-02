package jaywalker.html;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jaywalker.util.XsltTransformer;
import jaywalker.xml.bind.TabPage;

public class TabPageDecorator {

	private static final HtmlOutputter OUTPUTTER_HTML = new HtmlOutputter();

	private final List contentList = new ArrayList();

	private final List xsltTransformerList = new ArrayList();

	private final List tabPaneDecoratorList = new ArrayList();

	private final TabPage tabPage;

	public TabPageDecorator(TabPage tabPage) {
		this.tabPage = tabPage;
	}

	public TabPage getTabPage() {
		return tabPage;
	}

	public void add(Content content) {
		contentList.add(content);
	}

	public void add(XsltTransformer transformer) {
		xsltTransformerList.add(transformer);
	}

	public void add(TabPaneDecorator decorator) {
		tabPaneDecoratorList.add(decorator);
	}

	private byte[] toolTip(String value, String tipTitle, String tipValue)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OUTPUTTER_HTML.toolTip(baos, value, tipTitle, tipValue);
		baos.flush();
		baos.close();
		return baos.toByteArray();
	}

	public void html(OutputStream os, String tabPaneName, String tabPaneVariable)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OUTPUTTER_HTML.h2(baos, "tab", new String(toolTip(tabPage.getShort(),
				tabPage.getLong(), tabPage.getDescription())));
		final String id = toId(tabPaneName);
		OUTPUTTER_HTML.javascript(baos, new String[] { addTabPage(
				tabPaneVariable, id) });
		outputContents(baos);
		outputXsltTransformers(baos);
		outputTabPanes(baos);
		baos.flush();
		baos.close();
		OUTPUTTER_HTML.div(os, "tab-page", id, baos.toByteArray());
	}

	private void outputContents(ByteArrayOutputStream baos) throws IOException {
		for (int i = 0; i < contentList.size(); i++) {
			Content content = (Content) contentList.get(i);
			baos.write(content.create(tabPage));
		}
	}

	private void outputXsltTransformers(ByteArrayOutputStream baos) {
		for (int i = 0; i < xsltTransformerList.size(); i++) {
			XsltTransformer transformer = (XsltTransformer) xsltTransformerList
					.get(i);
			transformer.write(baos);
		}
	}

	private void outputTabPanes(ByteArrayOutputStream baos) throws IOException {
		for (int i = 0; i < tabPaneDecoratorList.size(); i++) {
			TabPaneDecorator tabPaneDecorator = (TabPaneDecorator) tabPaneDecoratorList
					.get(i);
			tabPaneDecorator.html(baos);
		}
	}

	protected String addTabPage(String tabPane, String id) {
		return tabPane + ".addTabPage( document.getElementById( \"" + id
				+ "\" ) );";
	}

	private String toId(String tabPane) {
		return tabPane.toLowerCase() + "-" + tabPage.getShort() + "-page";
	}

}
