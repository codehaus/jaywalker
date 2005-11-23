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

import java.util.Map;
import java.util.HashMap;
import java.net.URL;

public class ClasslistElementCache {
    private Map map = new HashMap();
    public void clear() {
        map.clear();
    }

    public void write(URL url, ClasslistElement cle) {
        map.put(url, cle);
    }

    public ClasslistElement read(URL url) {
        return (ClasslistElement) map.get(url);
    }
}
