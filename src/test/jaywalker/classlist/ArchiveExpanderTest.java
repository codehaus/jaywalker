/**
 * Copyright 2005 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jaywalker.classlist;

import jaywalker.testutil.Path;
import jaywalker.util.FileSystem;
import jaywalker.util.ThreadHelper;
import jaywalker.util.URLHelper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ArchiveExpanderTest extends JayWalkerTestCase {
    public void testExpandOneLevelJar() throws IOException, URISyntaxException {

        // Clean up any temporary files
        final File expandedArchiveFile = new URLHelper().toEncodedFile(new URL("jar:" + Path.FILE_TEST1_JAR.toURL().toString() + "!/"));
        FileSystem.delete(expandedArchiveFile);
        assertFalse(expandedArchiveFile.exists());

        // Create Classlist element
        // Show that the classlist element's file representation does not exists
        assertTrue(Path.FILE_TEST1_JAR.exists());
        URL url = new URL("jar:" + Path.FILE_TEST1_JAR.toURL().toString() + "!/SerializableImpl.class");
        ClasslistElementFactory factory = new ClasslistElementFactory();
        ClasslistElement cle = factory.create(url);
        assertTrue(toCacheFile(cle).exists());
        assertTrue(cle instanceof ClassElement);

        // Expand the archive into the temporary directory
        // Show that the classlist element's file representation exists
        new ArchiveExpander().expand(Path.FILE_TEST1_JAR.toURL());
        new ThreadHelper().verify(Path.FILE_TEST1_JAR.toURL());
        assertTrue(toCacheFile(cle).exists());

        // Prove that caching is working
        long lastModified = toCacheFile(cle).lastModified();
        new ArchiveExpander().expand(Path.FILE_TEST1_JAR.toURL());
        new ThreadHelper().verify(Path.FILE_TEST1_JAR.toURL());
        File file = toCacheFile(cle);
        assertTrue(file.exists());
        assertEquals(lastModified, file.lastModified());
    }

	private File toCacheFile(ClasslistElement cle) throws URISyntaxException {
		URL cacheUrl = new URLHelper().toBaseContainerUrl(cle.getURL());
        File file = new File(new URI(cacheUrl.toString()));
		return file;
	}

//    public void testExpandMultiLevelJar() throws IOException, URISyntaxException {
//
//        // Clean up any temporary files
//        final URLHelper helper = new URLHelper();
//        final String URL_FILE_TEST4_JAR_PREFIX = "jar:" + Path.FILE_TEST4_JAR.toURL().toString();
//        FileSystem.delete(helper.toEncodedFile(new URL(URL_FILE_TEST4_JAR_PREFIX + "!/")));
//        FileSystem.delete(helper.toEncodedFile(new URL(URL_FILE_TEST4_JAR_PREFIX + "!/jaywalker-test3.jar" + "!/")));
//        FileSystem.delete(helper.toEncodedFile(new URL(URL_FILE_TEST4_JAR_PREFIX + "!/jaywalker-test3.jar!/jaywalker-test1.jar" + "!/")));
//
//        // Create Classlist element
//        // Show that the classlist element's file representation does not exists
//        assertTrue(Path.FILE_TEST4_JAR.exists());
//        URL url = new URL(URL_FILE_TEST4_JAR_PREFIX + "!/jaywalker-test3.jar!/jaywalker-test1.jar!/SerializableImpl.class");
//        ClasslistElementFactory factory = new ClasslistElementFactory();
//        ClasslistElement cle = factory.create(url);
//        new ThreadHelper().verify(url);
//        assertFalse(cle.getEncodedFile().exists());
//        assertTrue(cle instanceof ClassElement);
//
//        // Expand the archive into the temporary directory
//        // Show that the classlist element's file representation exists
//        new ArchiveExpander().expand(Path.FILE_TEST4_JAR.toURL());
//        new ThreadHelper().verify(Path.FILE_TEST4_JAR.toURL());
//        final URL test3Url = new URL(URL_FILE_TEST4_JAR_PREFIX + "!/jaywalker-test3.jar");
//		new ArchiveExpander().expand(test3Url);
//		new ThreadHelper().verify(test3Url);
//        final URL test1Url = new URL(URL_FILE_TEST4_JAR_PREFIX + "!/jaywalker-test3.jar!/jaywalker-test1.jar");
//		new ArchiveExpander().expand(test1Url);
//		new ThreadHelper().verify(test1Url);
//        assertTrue(cle.getEncodedFile().exists());
//
//        // Prove that caching is working
//        long lastModified = cle.getEncodedFile().lastModified();
//        new ArchiveExpander().expand(Path.FILE_TEST4_JAR.toURL());
//        new ThreadHelper().verify(Path.FILE_TEST4_JAR.toURL());
//        assertTrue(cle.getEncodedFile().exists());
//        assertEquals(lastModified, cle.getEncodedFile().lastModified());
//    }

}
