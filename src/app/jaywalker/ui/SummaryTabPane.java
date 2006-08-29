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
			addArchiveTabPage("Metrics", "Archive Metric Report",
					"Summary metrics report on Archive elements");
			addArchiveTabPage("Resolved",
					"Resolved Archive Dependencies Report",
					"Dependencies report on resolved Archive elements");
			addArchiveTabPage("Cycle", "Archive Cycles Report",
					"Cyclic dependencies report on resolved Archive elements");
			tabCreator.addTabToPane(archiveTabPane, "Archive",
					"Archive Summary Reports",
					"Summary Reports specific to Archives");
		}

		if (properties.containsKey("package")) {
			addPackageTabPage("Metrics", "Package Metric Report",
					"Summary metrics report on Package elements");
			addPackageTabPage("Resolved",
					"Resolved Archive Dependencies Report",
					"Dependencies report on resolved Package elements");
			addPackageTabPage("Cycle", "Archive Cycles Report",
					"Cyclic dependencies report on resolved Archive elements");
			tabCreator.addTabToPane(packageTabPane, "Package",
					"Package Summary Reports",
					"Summary Reports specific to Packages");
		}

		if (properties.containsKey("class")) {
			addClassTabPage(
					"Collision",
					"Class Collision Report",
					"Report identifying those classes that have name collisions with other classes found during the walk");
			addClassTabPage(
					"Conflict",
					"Class Conflict Report",
					"Report identifying those Serializable classes whose serialVersionUid is conflicting with similar Serializable classes found during the walk");
			addClassTabPage("Unresolved", "Unresolved Classes Report",
					"Dependencies report on unresolved Class elements");
			addClassTabPage("Cycle", "Class Cycles Report",
					"Cyclic dependencies report on Class elements");
			tabCreator.addTabToPane(classTabPane, "Class",
					"Class Summary Reports",
					"Summary Reports specific to Classes");
		}

		return tabCreator.getBytes();
	}

	private void addArchiveTabPage(String name, String title, String description)
			throws IOException {
		addTabPage(archiveTabPane, "archive", name, title, description);
	}

	private void addPackageTabPage(String name, String title, String description)
			throws IOException {
		addTabPage(packageTabPane, "package", name, title, description);
	}

	private void addClassTabPage(String name, String title, String description)
			throws IOException {
		addTabPage(classTabPane, "class", name, title, description);
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
