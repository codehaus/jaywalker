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

import java.net.URL;

public class FileElement extends ClasslistElement {

    public static class Creator implements ClasslistElementCreator {
        public boolean isType(URL url) {
        	return true;
        }

        public ClasslistElement create(URL url) {
            return new FileElement(url);
        }
    }

    public FileElement(URL url) {
        super(url);
    }

}
