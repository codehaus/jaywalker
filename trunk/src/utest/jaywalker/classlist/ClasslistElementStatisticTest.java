package jaywalker.classlist;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import jaywalker.testutil.Path;

import junit.framework.TestCase;

public class ClasslistElementStatisticTest extends TestCase {

	private interface FileCondition {
		boolean isTrue(File file);
	}

	public void testShouldTrackFileElementsForDirectory()
			throws MalformedURLException {
		File parentFile = Path.DIR_BUILD_APP;
		ClasslistElementStatistic statistic = new ClasslistElementStatistic();
		createCountEventsAndVisitEach(parentFile, statistic);
		int count = fileCount(parentFile, new FileCondition() {
			public boolean isTrue(File file) {
				return !file.isDirectory();
			}
		});
		assertTrue(count > 0);
		assertEquals(count, statistic.getFileCountFor(parentFile.toURL()));
	}

	private int fileCount(File parent, FileCondition condition) {
		if (!parent.isDirectory()) {
			return 0;
		}
		File[] files = parent.listFiles();
		int count = 0;
		for (int i = 0; i < files.length; i++) {
			if (condition.isTrue(files[i])) {
				count++;
			}
		}
		return count;
	}

	private void createCountEventsAndVisitEach(File file,
			ClasslistElementStatistic statistic) throws MalformedURLException {
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			ClasslistElement element;
			if (!files[i].isDirectory()) {
				element = new FileElement(files[i].toURL());
			} else {
				element = new DirectoryContainer(files[i].toURL());
			}
			ClasslistElementEvent event = new ClasslistElementEvent(element);
			statistic.classlistElementVisited(event);
		}
	}

	public void testShouldTrackDirectoryElementsForDirectory()
			throws MalformedURLException {
		File parentFile = Path.DIR_BUILD_APP;
		ClasslistElementStatistic statistic = new ClasslistElementStatistic();
		createCountEventsAndVisitEach(parentFile, statistic);
		int count = fileCount(parentFile, new FileCondition() {
			public boolean isTrue(File file) {
				return file.isDirectory();
			}
		});
		assertTrue(count > 0);
		assertEquals(count, statistic.getDirectoryCountFor(parentFile.toURL()));
	}
}
