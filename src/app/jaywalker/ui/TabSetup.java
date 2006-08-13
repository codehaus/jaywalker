package jaywalker.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import jaywalker.util.XsltTransformer;
import jaywalker.util.XsltTransformerMap;

public class TabSetup implements Content {

	private final Properties properties;

	private TabPane mainTabPane = new TabPane("Main");

	private TabPane archiveTabPane = new TabPane("Archive");

	private TabPane packageTabPane = new TabPane("Package");

	private TabPane classTabPane = new TabPane("Class");

	private XsltTransformerMap transformerMap;

	public TabSetup(XsltTransformerMap transformerMap) {
		this.transformerMap = transformerMap;
		this.properties = new Properties();
		properties.setProperty("archive", "metrics,resolved,cycle");
		properties.setProperty("package", "metrics,resolved,cycle");
		properties.setProperty("class", "collision,conflict,unresolved,cycle");
	}

	public byte[] getBytes() throws IOException {

		if (properties.containsKey("archive")) {
			addArchiveTabPage("Metrics");
			addArchiveTabPage("Resolved");
			addArchiveTabPage("Cycle");
			addTab(archiveTabPane, "Archive");
		}

		if (properties.containsKey("package")) {
			addPackageTabPage("Metrics");
			addPackageTabPage("Resolved");
			addPackageTabPage("Cycle");
			addTab(packageTabPane, "Package");
		}

		if (properties.containsKey("class")) {
			addClassTabPage("Collision");
			addClassTabPage("Conflict");
			addClassTabPage("Unresolved");
			addClassTabPage("Cycle");
			addTab(classTabPane, "Class");
		}

		return toBytes(mainTabPane);
	}

	private byte[] toBytes(TabPane tabPane) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		tabPane.write(baos);
		baos.flush();
		baos.close();
		return baos.toByteArray();
	}

	private void addTab(TabPane tabPane, String tabPageName) throws IOException {
		mainTabPane.add(createTabPage(tabPane, tabPageName));
	}

	private TabPage createTabPage(TabPane tabPane, String tabPageName)
			throws IOException {
		final TabPage tabPage = new TabPage(tabPageName);
		tabPage.add("<br/>".getBytes());
		tabPage.add(toBytes(tabPane));
		return tabPage;
	}

	private void addArchiveTabPage(String name) throws IOException {
		addTabPage(archiveTabPane, "archive", name);
	}

	private void addPackageTabPage(String name) throws IOException {
		addTabPage(packageTabPane, "package", name);
	}

	private void addClassTabPage(String name) throws IOException {
		addTabPage(classTabPane, "class", name);
	}

	private void addTabPage(TabPane tabPane, String scope, String name)
			throws IOException {
		String value = name.toLowerCase();
		if (properties.containsKey(scope)) {
			if (properties.getProperty(scope).indexOf(value) != -1) {
				final XsltTransformer xsltTransformer = transformerMap.get(
						scope, value);
				tabPane.add(new TabPage(name, toBytes(xsltTransformer)));
			}
		}
	}

	private byte[] toBytes(final XsltTransformer xsltTransformer)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		xsltTransformer.write(baos);
		baos.flush();
		baos.close();
		return baos.toByteArray();
	}
}
