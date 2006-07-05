package jaywalker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import jaywalker.testutil.Path;

import junit.framework.TestCase;

public class JayWalkerRuntimeTest extends TestCase {

	private File file;

	private String version;

	public void setUp() throws Exception {
		super.setUp();
		Properties properties = new Properties();
		properties.load(new FileInputStream("build.properties"));
		version = properties.getProperty("project.version");
		file = new File("jaywalker-" + version + ".jar");
		assertTrue(file.exists());
	}

	public void testShouldRetrieveVersionInJayWalkerJarFile()
			throws FileNotFoundException, IOException {
		JayWalkerRuntime runtime = new JayWalkerRuntime(file);
		assertEquals(version, runtime.getVersion());
	}

	public void testShouldRetrieveDefaultVersionInJayWalkerJarFile()
			throws FileNotFoundException, IOException {
		File bogusFile = Path.FILE_TEST1_JAR;
		JayWalkerRuntime runtime = new JayWalkerRuntime(bogusFile);
		assertEquals("DEV", runtime.getVersion());
	}

	public void testShouldRetrieveTitleInJayWalkerJarFile()
			throws FileNotFoundException, IOException {
		JayWalkerRuntime runtime = new JayWalkerRuntime(file);
		assertEquals("jaywalker", runtime.getTitle());
	}

	public void testShouldRetrieveDefaultTitleInJayWalkerJarFile()
			throws FileNotFoundException, IOException {
		File bogusFile = Path.FILE_TEST1_JAR;
		JayWalkerRuntime runtime = new JayWalkerRuntime(bogusFile);
		assertEquals("UNKNOWN", runtime.getTitle());
	}

}
