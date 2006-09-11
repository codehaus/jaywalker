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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import jaywalker.testutil.Path;
import jaywalker.util.FileSystem;
import jaywalker.util.URLHelper;

public class ArchiveContainerTest extends JayWalkerTestCase {

	private final File file = new File(Path.DIR_TEMP.getAbsolutePath()
			+ File.separator 
			+ "cmdline"
			+ File.separator
			+ "JayWalkerTaskTest.testUnifiedReportForTest1Test2Archive"
			+ File.separator + "report.xml");
	
	public ArchiveContainerTest(String name) {
		super(name);
	}

	public void testOneLevelArchiveContainerCreation() throws IOException, URISyntaxException {

		if (!file.exists()) {
			executeTarget("testUnifiedReportForTest1Test2Archive");
		}
		assertTrue(file.exists());

        // Clean up any temporary files
        final File expandedURL = new URLHelper().toEncodedFile(new URL("jar:" + Path.FILE_TEST1_JAR.toURL().toString() + "!/"));
        FileSystem.delete(expandedURL);

        // Create Classlist element
        assertTrue(Path.FILE_TEST1_JAR.exists());
        ClasslistElementFactory factory = new ClasslistElementFactory();
        ClasslistElement cle = factory.create(Path.FILE_TEST1_JAR.toURL());
        assertNotNull(cle);
        assertTrue(cle instanceof ArchiveContainer);

        assertElementByKeywordExistsForType((ArchiveContainer) cle, "/SerializableImpl.class", ClassElement.class);
        ClasslistContainer metaInfDir = (ClasslistContainer) assertElementByKeywordExistsForType((ArchiveContainer) cle, "/META-INF/", DirectoryContainer.class);
        ClasslistElement[] elements = metaInfDir.getClasslistElements();
        assertEquals(1,elements.length);
    }

    public void testMultiLevelArchiveContainerCreation() throws IOException, URISyntaxException {

        // Clean up any temporary files
        final File expandedURL = new URLHelper().toEncodedFile(new URL("jar:" + Path.FILE_TEST4_JAR.toURL().toString() + "!/"));
        FileSystem.delete(expandedURL);

        // Create Classlist element
        assertTrue(Path.FILE_TEST4_JAR.exists());
        ClasslistElementFactory factory = new ClasslistElementFactory();
        ClasslistElement cle = factory.create(Path.FILE_TEST4_JAR.toURL());
        assertNotNull(cle);
        assertTrue(cle instanceof ArchiveContainer);

        cle = assertElementByKeywordExistsForType((ArchiveContainer) cle, "/jaywalker-test3.jar", ArchiveContainer.class);
        cle = assertElementByKeywordExistsForType((ArchiveContainer) cle, "/jaywalker-test1.jar", ArchiveContainer.class);
        assertElementByKeywordExistsForType((ArchiveContainer) cle, "/SerializableImpl.class", ClassElement.class);

    }

}
