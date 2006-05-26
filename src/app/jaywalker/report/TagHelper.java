/**
 * Copyright 2005 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jaywalker.report;

import java.net.URL;
import java.util.Stack;

public class TagHelper {
    public String toSpaces(int spaces) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= spaces; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    public String createDependencyTags(String tagType, String nestedTagName, String nestedTagType, URL [] urls, Object [] values, Stack parentUrlStack) {
        StringBuffer sb = new StringBuffer();
        if (urls != null && urls.length > 0) {
            sb.append(toSpaces(parentUrlStack.size()));
            sb.append("<dependency type=\"").append(tagType).append("\" value=\"").append(urls.length);
            sb.append("\">\n");
            for (int i = 0; i < urls.length; i++) {
                sb.append(toSpaces(parentUrlStack.size() + 1));
                sb.append("<");
                sb.append(nestedTagName);
                sb.append(createAttribute("type", nestedTagType));
                sb.append(createAttribute("url", urls[i]));
                sb.append(createAttribute("value", values[i]));
                sb.append("/>\n");
            }
            sb.append(toSpaces(parentUrlStack.size()));
            sb.append("</dependency>\n");
        }
        return sb.toString();
    }

    private String createAttribute(String name, Object value) {
        return " " + name + "=\"" + value + "\"";
    }

}
