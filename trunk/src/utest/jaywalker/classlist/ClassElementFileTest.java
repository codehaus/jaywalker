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
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import jaywalker.testutil.Path;
import jaywalker.util.FileSystem;
import jaywalker.util.URLHelper;

public class ClassElementFileTest extends JayWalkerTestCase {
    public void testShouldReturnValidPropertiesFromClsFile() throws IOException, URISyntaxException {

        URLHelper helper = new URLHelper();

        // Clean up any temporary files
        final URL parentUrl = Path.FILE_TEST1_JAR.toURL();
        URL url = new URL("jar:" + Path.FILE_TEST1_JAR.toURL() + "!/SerializableImpl.class");

        final File expandedURL = helper.toEncodedFile(new URL("jar:" + parentUrl.toString() + "!/"));
        FileSystem.delete(expandedURL);

        // Expand the archive into the temporary directory
        // Show that the classlist element's file representation exists
        ArchiveExpander expander = new ArchiveExpander();
        expander.expand(parentUrl);

        ClassElementFile classElementFile = new ClassElementFile(url);
        assertEquals("SerializableImpl", classElementFile.getClassName());
        assertEquals("java.lang.Object", classElementFile.getSuperClassName());

    }

}
