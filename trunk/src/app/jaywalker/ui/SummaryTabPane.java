package jaywalker.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import jaywalker.util.XsltTransformer;
import jaywalker.util.XsltTransformerMap;

public class SummaryTabPane implements Content {

	private final Properties properties;

	private TabPane mainTabPane = new TabPane("Main");

	private TabPane archiveTabPane = new TabPane("Archive");

	private TabPane packageTabPane = new TabPane("Package");

	private TabPane classTabPane = new TabPane("Class");

	private XsltTransformerMap transformerMap;

	private TabCreator tabCreator = new TabCreator(mainTabPane);

	public SummaryTabPane(XsltTransformerMap transformerMap) {
		this.transformerMap = transformerMap;
		this.properties = new Properties();
		properties.setProperty("archive", "metrics,resolved,cycle");
		properties.setProperty("package", "metrics,resolved,cycle");
		properties.setProperty("class", "collision,conflict,unresolved,cycle");
	}

	public byte[] getBytes() throws IOException {

		if (properties.containsKey("archive")) {
			addArchiveTabPage("Metrics", "Archive Metric Report");
			addArchiveTabPage("Resolved",
					"Resolved Archive Dependencies Report");
			addArchiveTabPage("Cycle", "Archive Cycles Report");
			tabCreator.addTabToPane(archiveTabPane, "Archive",
					"Archive Summary Reports");
		}

		if (properties.containsKey("package")) {
			addPackageTabPage("Metrics", "Package Metric Report");
			addPackageTabPage("Resolved",
					"Resolved Archive Dependencies Report");
			addPackageTabPage("Cycle", "Archive Cycles Report");
			tabCreator.addTabToPane(packageTabPane, "Package",
					"Package Summary Reports");
		}

		if (properties.containsKey("class")) {
			addClassTabPage("Collision", "Class Collision Report");
			addClassTabPage("Conflict", "Class Conflict Report");
			addClassTabPage("Unresolved", "Unresolved Classes Report");
			addClassTabPage("Cycle", "Class Cycles Report");
			tabCreator.addTabToPane(classTabPane, "Class",
					"Class Summary Reports");
		}

		return tabCreator.getBytes();
	}

	private void addArchiveTabPage(String name, String description)
			throws IOException {
		addTabPage(archiveTabPane, "archive", name, description);
	}

	private void addPackageTabPage(String name, String description)
			throws IOException {
		addTabPage(packageTabPane, "package", name, description);
	}

	private void addClassTabPage(String name, String description)
			throws IOException {
		addTabPage(classTabPane, "class", name, description);
	}

	private void addTabPage(TabPane tabPane, String scope, String name,
			String description) throws IOException {
		String value = name.toLowerCase();
		if (properties.containsKey(scope)) {
			if (properties.getProperty(scope).indexOf(value) != -1) {
				final XsltTransformer xsltTransformer = transformerMap.get(
						scope, value);
				tabPane.add(new TabPage(name, description,
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
