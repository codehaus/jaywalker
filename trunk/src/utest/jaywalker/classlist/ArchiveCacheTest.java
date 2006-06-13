package jaywalker.classlist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;

import jaywalker.testutil.Path;

import org.apache.bcel.classfile.JavaClass;

public class ArchiveCacheTest extends JayWalkerTestCase {

	public void testShouldRetrieveJavaClassFromClassFile()
			throws MalformedURLException, FileNotFoundException {
		final File file = Path.FILE_CLASSLIST_ELEMENT_FACTORY_CLASS;
		FileChannel channel = new FileInputStream(file).getChannel();
		ArchiveCache cache = new ArchiveCache(file.toURL());
		JavaClass javaClass = cache.toJavaClass(file.getName(), channel, 0,
				file.length());
		assertNotNull(javaClass);
	}

	public void testShouldRetrieveJavaClassFromArchiveCache()
			throws IOException {
		ArchiveExpander expander = new ArchiveExpander();
		URL url = Path.FILE_TEST1_JAR.toURL();
		expander.expand(url);
		ArchiveCache cache = new ArchiveCache(url);
		JavaClass javaClass = cache.toJavaClass("SerializableImpl.class");
		assertNotNull(javaClass);
	}

}
