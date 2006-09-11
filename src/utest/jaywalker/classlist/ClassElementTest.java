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
import jaywalker.util.URLHelper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ClassElementTest extends JayWalkerTestCase {

    public ClassElementTest(String name) {
		super(name);
	}

	public void testShouldCreateValidInstanceFromAClassFile() throws IOException, URISyntaxException {
        URL url = Path.FILE_CLASSLIST_ELEMENT_FACTORY_CLASS.toURL();
        final ClassElement classElement = new ClassElement(url);
        assertNotNull(classElement);
        assertTrue(new File(new URI(classElement.getURL().toString())).exists());
    }

    public void testShouldCreateValidInstanceFromArchivedClassFile() throws IOException, URISyntaxException {
        URL url = new URL("jar:" + Path.FILE_TEST1_JAR.toURL() + "!/SerializableImpl.class");
        final ClassElement classElement = new ClassElement(url);
        assertNotNull(classElement);
        URL cacheUrl = new URLHelper().toBaseContainerUrl(classElement.getURL());
        assertTrue(new File(new URI(cacheUrl.toString())).exists());
    }

    public void testShouldCreateValidInstanceFromNestedArchivedClassFile() throws IOException, URISyntaxException {
        URL url = new URL("jar:" + Path.FILE_TEST4_JAR.toURL() + "!/jaywalker-test3.jar!/jaywalker-test1.jar!/SerializableImpl.class");
        final ClassElement classElement = new ClassElement(url);
        assertNotNull(classElement);
        URL cacheUrl = new URLHelper().toBaseContainerUrl(classElement.getURL());
        ArchiveCache cache = new ArchiveCache(cacheUrl);
        assertTrue(cache.getArchiveFile().exists());
    }


}
