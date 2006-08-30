package jaywalker.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import jaywalker.util.ConfigParser;
import jaywalker.util.XsltTransformer;
import jaywalker.util.XsltTransformerMap;

public class SummaryTabPane implements Content {

	private final Properties properties;

	private TabPane mainTabPane = new TabPane("Main");

	private TabPane archiveTabPane = new TabPane("Archive");

	private TabPane packageTabPane = new TabPane("Package");

	private TabPane classTabPane = new TabPane("Class");

	private XsltTransformerMap transformerMap;

	private TabPageCreator tabCreator = new TabPageCreator(mainTabPane);

	private final ConfigParser config;

	public SummaryTabPane(XsltTransformerMap transformerMap) {
		this.transformerMap = transformerMap;
		this.properties = new Properties();
		properties.setProperty("archive", "metrics,resolved,cycle");
		properties.setProperty("package", "metrics,resolved,cycle");
		properties.setProperty("class", "collision,conflict,unresolved,cycle");

		try {
			config = new ConfigParser("jaywalker-config.xml");
		} catch (Exception e) {
			throw new RuntimeException(
					"Exception thrown while trying to parse jaywalker-config.xml",
					e);
		}

	}

	public byte[] getBytes() throws IOException {

		try {

			if (properties.containsKey("archive")) {
				addTabPage(archiveTabPane, "archive", "metrics");
				addTabPage(archiveTabPane, "archive", "resolved");
				addTabPage(archiveTabPane, "archive", "cycle");
				tabCreator.addTabToPane(archiveTabPane, config.lookupValue(
						"archive", "short"), config.lookupValue("archive",
						"long"), config.lookupValue("archive", "description"));
			}

			if (properties.containsKey("package")) {
				addTabPage(packageTabPane, "package", "metrics");
				addTabPage(packageTabPane, "package", "resolved");
				addTabPage(packageTabPane, "package", "cycle");
				tabCreator.addTabToPane(packageTabPane, config.lookupValue(
						"package", "short"), config.lookupValue("package",
						"long"), config.lookupValue("package", "description"));
			}

			if (properties.containsKey("class")) {
				addTabPage(classTabPane, "class", "collision");
				addTabPage(classTabPane, "class", "conflict");
				addTabPage(classTabPane, "class", "unresolved");
				addTabPage(classTabPane, "class", "cycle");
				tabCreator.addTabToPane(classTabPane, config.lookupValue(
						"class", "short"), config.lookupValue("class", "long"),
						config.lookupValue("class", "description"));
			}

			return tabCreator.getBytes();

		} catch (Exception e) {
			throw new RuntimeException(
					"Exception thrown while trying to parse jaywalker-config.xml",
					e);
		}

	}

	private void addTabPage(TabPane tabPane, String scope, String type)
			throws TransformerException, IOException {
		String name = config.lookupValue(scope, type, "short");
		String title = config.lookupValue(scope, type, "long");
		String description = config.lookupValue(scope, type, "description");
		addTabPage(tabPane, scope, name, title, description);
	}

	private void addTabPage(TabPane tabPane, String scope, String name,
			String title, String description) throws IOException {
		String value = name.toLowerCase();
		if (properties.containsKey(scope)) {
			if (properties.getProperty(scope).indexOf(value) != -1) {
				final XsltTransformer xsltTransformer = transformerMap.get(
						scope, value);
				tabPane.add(new TabPage(name, title, description,
						toBytes(xsltTransformer)));
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
