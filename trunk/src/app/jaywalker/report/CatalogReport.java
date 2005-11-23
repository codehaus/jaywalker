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
package jaywalker.report;

import jaywalker.classlist.ClasslistContainer;
import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementEvent;
import jaywalker.classlist.ClasslistElementListener;

import java.util.Stack;

public class CatalogReport implements ClasslistElementListener {
    StringBuffer sb = new StringBuffer();
    Stack stack = new Stack();

    public void classlistElementVisited(ClasslistElementEvent event) {
        final ClasslistElement element = event.getElement();
        if (isParentOnStackTop(element)) {
            while (isParentOnStackTop(element)) {
                stack.pop();
                sb.append(toSpaces(stack.size()));
                sb.append("</container>\n");
            }
        }

        sb.append(toSpaces(stack.size()));

        if (element instanceof ClasslistContainer) {
            sb.append("<container type=\"").append(element.getType()).append("\"");
            sb.append(" url=\"").append(element.getURL()).append("\"");
            sb.append(">\n");
            stack.push(element.getURL());
        } else {
            sb.append("<element type=\"").append(element.getType()).append("\"");
            sb.append(" url=\"").append(element.getURL()).append("\"");
            sb.append("/>\n");
        }
    }

    private boolean isParentOnStackTop(ClasslistElement element) {
        return !stack.isEmpty() && !element.getContainer().getURL().equals(stack.peek());
    }

    private String toSpaces(int spaces) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= spaces; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    public String toString() {
        final String header = "<?xml version=\"1.0\"?>\n";
        final StringBuffer sb = new StringBuffer(header);
        sb.append("<report type=\"catalog\"");
        int size = stack.size();

        if (size == 0) {
            sb.append("/>");
            return sb.toString();
        }

        sb.append(">\n");

        for (int i = 0; i < size; i++) {
            sb.append(toSpaces(size - i));
            sb.append("</container>\n");
        }

        sb.append("</report>");
        return sb.toString();
    }
}
