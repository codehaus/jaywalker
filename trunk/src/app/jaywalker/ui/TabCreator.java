package jaywalker.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TabCreator implements Content {

	private TabPane mainTabPane;

	public TabCreator(TabPane mainTabPane) {
		this.mainTabPane = mainTabPane;
	}

	public byte[] getBytes() throws IOException {
		return toBytes(mainTabPane);
	}

	public void addTabToPane(TabPane tabPane, String tabPageName,
			String tabPageTitle, String tabPageDescription) throws IOException {
		mainTabPane.add(createTabPage(tabPane, tabPageName, tabPageTitle,
				tabPageDescription));
	}

	private TabPage createTabPage(TabPane tabPane, String tabPageName,
			String tabPageTitle, String tabPageDescription) throws IOException {
		final TabPage tabPage = new TabPage(tabPageName, tabPageTitle,
				tabPageDescription);
		tabPage.add("<br/>".getBytes());
		tabPage.add(toBytes(tabPane));
		return tabPage;
	}

	private byte[] toBytes(TabPane tabPane) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		tabPane.write(baos);
		baos.flush();
		baos.close();
		return baos.toByteArray();
	}

}
