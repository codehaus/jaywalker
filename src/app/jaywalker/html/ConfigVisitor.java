package jaywalker.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import jaywalker.util.XsltTransformer;
import jaywalker.xml.bind.Config;
import jaywalker.xml.bind.TabPage;
import jaywalker.xml.bind.TabPane;

public class ConfigVisitor {

	private final OutputStream os;

	public ConfigVisitor(OutputStream os) {
		this.os = os;
	}

	public void visit(String filename) {
		visit(ConfigVisitor.class.getResourceAsStream("/META-INF/xml/"
				+ filename));
	}

	public void visit(InputStream is) {
		try {
			JAXBContext jc = JAXBContext.newInstance("jaywalker.xml.bind",
					ConfigVisitor.class.getClassLoader());
			Unmarshaller u = jc.createUnmarshaller();
			Config config = (Config) u.unmarshal(is);
			ConfigDecorator decorator = new ConfigDecorator(config);
			visit(decorator);
		} catch (JAXBException e) {
			throw new RuntimeException(
					"Exception thrown while processing XML binding for the configuration file.",
					e);
		}
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
			TabPageDecorator tabPageDecorator = new TabPageDecorator(tabPage);
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

	private void addContent(TabPageDecorator tabPageDecorator, String className) {
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
