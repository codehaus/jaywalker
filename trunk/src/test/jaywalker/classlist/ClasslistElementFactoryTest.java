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
import jaywalker.util.ResourceLocator;
import jaywalker.util.Shell;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class ClasslistElementFactoryTest extends TestCase {
    public void setUp() throws IOException {
        // We deliberately don't extend JayWalkerTestCase so we can exercise the negative case when the cache d.n.e.
        ResourceLocator.instance().register("tempDir", Shell.toWorkingDir(Path.DIR_TEMP.getAbsolutePath()));
    }

    public void tearDown() {
        ResourceLocator.instance().clear();
    }

    private void assertValidClasslistElement(ClasslistElement classlistElement, Class clazz) {
        assertNotNull(classlistElement);
        assertSame(clazz, classlistElement.getClass());
    }

    public void testShouldCreateValidClassElement() throws MalformedURLException, URISyntaxException {
        URL url = Path.FILE_CLASSLIST_ELEMENT_FACTORY_CLASS.toURL();
        final ClasslistElement classlistElement = new ClasslistElementFactory().create(url);
        assertValidClasslistElement(classlistElement, ClassElement.class);
        assertTrue(classlistElement.getEncodedFile().exists());
    }

    public void assertClassElement(ClasslistElement element) throws MalformedURLException, URISyntaxException {
        assertValidClasslistElement(element, ClassElement.class);
        assertTrue(element.getEncodedFile().exists());
    }

    public void testShouldCreateValidArchivedClassElement() throws MalformedURLException, URISyntaxException {
        URL url = new URL("jar:" + Path.FILE_TEST1_JAR.toURL() + "!/SerializableImpl.class");
        assertClassElement(new ClasslistElementFactory().create(url));
    }

    public void testShouldCreateValidDirectoryElement() throws MalformedURLException {
        URL url = Path.DIR_BUILD_APP.toURL();
        int fileCnt = Path.DIR_BUILD_APP.listFiles().length;
        final DirectoryContainer element = (DirectoryContainer) new ClasslistElementFactory().create(url);
        assertDirectoryContainer(element, fileCnt);
    }

    private void assertDirectoryContainer(ClasslistContainer element, int fileCnt) {
        assertNotNull(element);
        final ClasslistElement[] elements = element.getClasslistElements();
        assertEquals(fileCnt, elements.length);
        for (int i = 0; i < elements.length; i++)
            assertEquals(element, elements[i].getContainer());
    }

    public void testShouldCreateValidElementsFromClasslist() throws MalformedURLException, URISyntaxException {
        String classlist = Path.FILE_CLASSLIST_ELEMENT_FACTORY_CLASS + File.pathSeparator + Path.DIR_BUILD_APP;
        int fileCnt = Path.DIR_BUILD_APP.listFiles().length;

        final ClasslistElement [] classlistElements = new ClasslistElementFactory().create(classlist);
        assertEquals(2, classlistElements.length);
        assertClassElement(classlistElements[0]);
        assertDirectoryContainer((ClasslistContainer) classlistElements[1],fileCnt);
    }

}
