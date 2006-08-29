package jaywalker.ui;

import java.io.IOException;

import junit.framework.TestCase;

public class TabCreatorTest extends TestCase {
	public void testShould() throws IOException {
		TabPane mainPane = new TabPane("main");
		TabCreator creator = new TabCreator(mainPane);
		TabPane tabPane1 = new TabPane("1");
		TabPane tabPane2 = new TabPane("2");
		TabPane tabPane3 = new TabPane("3");
		creator.addTabToPane(tabPane1, "1", "1");
		creator.addTabToPane(tabPane2, "2", "2");
		creator.addTabToPane(tabPane3, "3", "3");
		assertNotNull(creator.getBytes());
	}
}
