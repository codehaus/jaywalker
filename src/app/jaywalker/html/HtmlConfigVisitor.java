package jaywalker.html;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import jaywalker.util.FileDecorator;
import jaywalker.util.XsltTransformer;
import jaywalker.xml.ConfigVisitor;
import jaywalker.xml.bind.Config;
import jaywalker.xml.bind.TabPage;
import jaywalker.xml.bind.TabPane;

public class HtmlConfigVisitor extends ConfigVisitor {

	private final FileDecorator reportFile;

	protected final OutputStream os;

	public HtmlConfigVisitor(OutputStream os, FileDecorator reportFile) {
		this.os = os;
		this.reportFile = reportFile;
	}

	public void accept() {
		accept(this);
	}

	public void accept(HtmlConfigVisitor visitor) {
		visitor.visit(new ConfigDecorator(config));
	}

	public void visit(ConfigDecorator configDecorator) {
		try {
			Config config = configDecorator.getConfig();
			List tabPaneList = config.getTabPane();
			for (int i = 0; i < tabPaneList.size(); i++) {
				TabPane tabPane = (TabPane) tabPaneList.get(i);
				TabPaneDecorator tabPaneDecorator = new TabPaneDecorator(
						tabPane);
				configDecorator.add(tabPaneDecorator);
				visit(tabPaneDecorator);
			}
			configDecorator.html(os);
		} catch (IOException e) {
			throw new RuntimeException(
					"Error while processing configuration file", e);
		}
	}

	public void visit(TabPaneDecorator tabPaneDecorator) throws IOException {
		TabPane tabPane = tabPaneDecorator.getTabPane();
		List tabPageList = tabPane.getTabPage();
		for (int i = 0; i < tabPageList.size(); i++) {
			TabPage tabPage = (TabPage) tabPageList.get(i);
			TabPageDecorator tabPageDecorator = new TabPageDecorator(tabPage,
					reportFile);
			tabPaneDecorator.add(tabPageDecorator);
			visit(tabPageDecorator);
		}
	}

	public void visit(TabPageDecorator tabPageDecorator) throws IOException {
		TabPage tabPage = tabPageDecorator.getTabPage();
		List tabPaneList = tabPage.getTabPane();
		String className = tabPage.getContent();
		addContent(tabPageDecorator, className);
		String xslt = tabPage.getXslt();
		addXsltTransformer(tabPageDecorator, xslt);
		for (int i = 0; i < tabPaneList.size(); i++) {
			TabPane tabPane = (TabPane) tabPaneList.get(i);
			TabPaneDecorator tabPaneDecorator = new TabPaneDecorator(tabPane);
			tabPageDecorator.add(tabPaneDecorator);
			visit(tabPaneDecorator);
		}
	}

	private void addXsltTransformer(TabPageDecorator tabPageDecorator,
			String xslt) {
		if (xslt != null) {
			tabPageDecorator.add(new XsltTransformer(xslt));
		}
	}

	protected void addContent(TabPageDecorator tabPageDecorator, String className) {
		if (className != null) {
			try {
				Class clazz = Class.forName(className);
				Content content = (Content) clazz.newInstance();
				tabPageDecorator.add(content);
			} catch (Exception e) {
				throw new RuntimeException(
						"Exception thrown while looking up content class: "
								+ className, e);
			}
		}
	}

}
