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
package jaywalker.util;

import jaywalker.testutil.Path;
import junit.framework.TestCase;

public class ResourceLocatorTest extends TestCase {

    public void tearDown() {
        ResourceLocator.instance().clear();        
    }

    public void testShouldReturnValidTempDirOnLookup() {
        final ResourceLocator locator = ResourceLocator.instance();
        locator.clear();
        locator.register("tempDir",Path.DIR_TEMP);
        assertSame(Path.DIR_TEMP,locator.lookup("tempDir"));
    }
    public void testShouldThrowResourceNotFoundExceptionOnLookup() {
        try {
            final ResourceLocator locator = ResourceLocator.instance();
            locator.clear();
            locator.lookup("tempDir");
            fail("ResourceNotFoundException should have been thrown");
        } catch ( ResourceNotFoundException e ) {
            // do nothing
        }
    }
}
