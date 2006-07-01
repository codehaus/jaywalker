package jaywalker.ant;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.tools.ant.Project;

import jaywalker.testutil.Path;
import junit.framework.TestCase;

public class JayWalkerTaskTest extends TestCase {

	private Project project;

	private JayWalkerTask task;

	public void setUp() {
		project = new Project();
		task = new JayWalkerTask();
		task.setProject(project);
	}

	public void testShouldCreateOnlyOneCommandlineJavaInstance() {
		assertSame(task.getCommandline(), task.getCommandline());
	}

	public void testShouldUpdateCommandlineJavaInstanceWhenOutDirGiven() {
		File file = Path.DIR_TEMP;
		task.setOutDir(file);
		String[] values = task.getCommandline().getCommandline();
		assertEquals(2, values.length);
		assertEquals("-outDir=" + file.getAbsolutePath(), values[1]);
	}

	public void testShouldUpdateCommandlineJavaInstanceWhenTempDirGiven() {
		File file = Path.DIR_TEMP;
		task.setTempDir(file);
		String[] values = task.getCommandline().getCommandline();
		assertEquals(2, values.length);
		assertEquals("-tempDir=" + file.getAbsolutePath(), values[1]);
	}

	public void testShouldUpdateCommandlineJavaInstanceWithClasslist() {
		String expected = Path.FILE_CLASSLIST_ELEMENT_FACTORY_CLASS
				+ File.pathSeparator
				+ Path.FILE_CLASSLIST_ELEMENT_VISITOR_CLASS;
		task.createClasslistArgument(expected);
		String[] values = task.getCommandline().getCommandline();
		assertEquals(2, values.length);
		assertEquals("-classlist=" + expected, values[1]);
	}

	public void testShouldUpdateCommandlineJavaInstanceWithProperties() {
		Properties properties = new Properties();
		properties.setProperty("1", "1");
		properties.setProperty("2", "2");
		properties.setProperty("3", "3");
		String[] values = task.toArguments(properties);
		assertEquals(3, values.length);
		assertEquals("-1=1", values[0]);
		assertEquals("-2=2", values[1]);
		assertEquals("-3=3", values[2]);
	}

	public void testShouldExecuteTaskAsForked() {
		File file = Path.DIR_TEMP;
		task.setOutDir(file);
		Classlist classlist = new Classlist();
		classlist.setFile(Path.FILE_CLASSLIST_ELEMENT_FACTORY_CLASS);
		task.addClasslist(classlist);
		task.execute();
	}

	public void testShouldReturnTopLevelJarUrlForEnclosedClass() throws MalformedURLException {
		URL url = new URL("jar:file:/C:/JayWalker/jaywalker-x.x-SNAPSHOT.jar!/"
				+ "jaywalker/ant/JayWalkerTask.class");
		assertEquals("file:/C:/JayWalker/jaywalker-x.x-SNAPSHOT.jar", task.toTopLevelJarUrl(url).toString());
	}
	
	public void testShouldReturnTopLevelJarUrlForIdentity() throws MalformedURLException {
		URL url = new URL("file:/C:/JayWalker/jaywalker-x.x-SNAPSHOT.jar");
		assertEquals("file:/C:/JayWalker/jaywalker-x.x-SNAPSHOT.jar", task.toTopLevelJarUrl(url).toString());
	}
	
}
