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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import org.apache.tools.ant.BuildFileTest;

import jaywalker.testutil.Path;
import jaywalker.util.ResourceLocator;
import jaywalker.util.Shell;
import jaywalker.util.URLHelper;
import junit.framework.TestCase;

public class JayWalkerTestCase extends BuildFileTest {

    public JayWalkerTestCase(String name) {
		super(name);
	}

	public void setUp() throws IOException {
		configureProject("build-ant-test.xml");
        ResourceLocator.instance().register("tempDir", Shell.toWorkingDir(Path.DIR_TEMP.getAbsolutePath()));
    }

    public void tearDown() {
        ResourceLocator.instance().clear();
    }

    public ClasslistElement findElementByKeyword(ClasslistContainer container, String keyword) {
        final ClasslistElement[] elements = container.getClasslistElements();
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].getURL().toString().endsWith(keyword)) {
                return elements[i];
            }
        }
        return null;
    }

    protected ClasslistElement assertElementByKeywordExistsForType(ArchiveContainer archive, String keyword, Class clazz) throws MalformedURLException, URISyntaxException {
        ClasslistElement element = findElementByKeyword(archive, keyword);
        assertNotNull(element);
        assertTrue(element.getClass() == clazz);
        URL cacheUrl = new URLHelper().toBaseContainerUrl(element.getURL());
        ArchiveCache cache = new ArchiveCache(cacheUrl);
        assertTrue(cache.getArchiveFile().exists());
        return element;
    }

    public void assertVisit(URL url, ClasslistElementListener listener) throws IOException {
        final ClasslistElement element = createClasslistElement(url);
        ClasslistElement [] elements = new ClasslistElement[]{element};
        ClasslistElementVisitor visitor = new ClasslistElementVisitor(elements);
        final ClasslistElementStatistic statisticListener = new ClasslistElementStatistic();
        visitor.addListener(statisticListener);
        visitor.addListener(listener);
        Date start = new Date();
        visitor.accept();
        System.out.println("Time to visit elements : " + (new Date().getTime() - start.getTime()));
        System.out.println(statisticListener);
    }

    protected ClasslistElement createClasslistElement(URL url) {
        ClasslistElementFactory factory = new ClasslistElementFactory();
        return factory.create(url);
    }
}
