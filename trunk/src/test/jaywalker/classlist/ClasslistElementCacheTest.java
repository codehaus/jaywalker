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

import jaywalker.util.ResourceLocator;
import jaywalker.testutil.Path;
import junit.framework.TestCase;

import java.net.MalformedURLException;
import java.net.URL;

public class ClasslistElementCacheTest extends TestCase {
    public void setUp() {
        ResourceLocator.instance().register("classlistElementCache", new ClasslistElementCache());
    }

    public void tearDown() {
        ClasslistElementCache cache = (ClasslistElementCache) ResourceLocator.instance().lookup("classlistElementCache");
        cache.clear();
    }

    public void testAddingToAndRetrievingFromCache() throws MalformedURLException {
        ClasslistElementCache cache = (ClasslistElementCache) ResourceLocator.instance().lookup("classlistElementCache");
        final URL url = Path.FILE_CLASSLIST_ELEMENT_FACTORY_CLASS.toURL();
        ClasslistElement cle = new ClassElement.Creator().create(url);
        cache.write(url, cle);
        assertSame(cle, cache.read(url));
    }
}
