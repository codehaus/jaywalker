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
import java.net.URISyntaxException;
import java.net.URL;

public class ArchiveContainerTest extends JayWalkerTestCase {
    public void testOneLevelArchiveContainerCreation() throws IOException, URISyntaxException {

        // Clean up any temporary files
        final File expandedURL = new URLHelper().toEncodedFile(new URL("jar:" + Path.FILE_TEST1_JAR.toURL().toString() + "!/"));
        FileSystem.delete(expandedURL);

        // Create Classlist element
        assertTrue(Path.FILE_TEST1_JAR.exists());
        ClasslistElementFactory factory = new ClasslistElementFactory();
        ClasslistElement cle = factory.create(Path.FILE_TEST1_JAR.toURL());
        new ThreadHelper().verify(Path.FILE_TEST1_JAR.toURL());
        assertNotNull(cle);
        assertTrue(cle instanceof ArchiveContainer);

        assertElementByKeywordExistsForType((ArchiveContainer) cle, "/SerializableImpl.class", ClassElement.class);
        DirectoryContainer metaInfDir = (DirectoryContainer) assertElementByKeywordExistsForType((ArchiveContainer) cle, "/META-INF/", DirectoryContainer.class);
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
        new ThreadHelper().verify(Path.FILE_TEST1_JAR.toURL());
        assertNotNull(cle);
        assertTrue(cle instanceof ArchiveContainer);

        cle = assertElementByKeywordExistsForType((ArchiveContainer) cle, "/jaywalker-test3.jar", ArchiveContainer.class);
        cle = assertElementByKeywordExistsForType((ArchiveContainer) cle, "/jaywalker-test1.jar", ArchiveContainer.class);
        assertElementByKeywordExistsForType((ArchiveContainer) cle, "/SerializableImpl.class", ClassElement.class);

    }

}
