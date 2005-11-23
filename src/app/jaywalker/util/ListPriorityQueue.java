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

import java.util.HashMap;
import java.util.Map;

public class ListPriorityQueue implements PriorityQueue {
    private Map map;

    protected static class Element {
        final Object obj;
        final Priority priority;
        Element previous;
        Element next;

        public Element(Object obj, Priority priority) {
            this.obj = obj;
            this.priority = priority;
            previous = this;
            next = this;
        }

        public String toString() {
            return obj + "," + priority;
        }

    }

    private Element head;

    public void init(Priority p) {
        head = new Element(null, p);
        map = new HashMap();
    }

    public void enqueue(Object obj, Priority p) {
        Element element1 = head.previous;
        Element element2 = new Element(obj, p);
        map.put(obj, element2);
        while (isThisBeforeThat(element1, element2)) {
            element1 = element1.previous;
        }
        insertThisAfterThat(element2, element1);
    }

    protected boolean isThisBeforeThat(Element element1, Element element2) {
        return element1.priority.isHigherThan(element2.priority);
    }

    protected void insertThisAfterThat(Element element1, Element element2) {
        element1.previous = element2;
        element1.next = element2.next;
        element2.next.previous = element1;
        element2.next = element1;
    }

    public void requeue(Object obj, Priority p) {
        Element element = (Element) map.get(obj);
        if (element != null) detach(element);
        enqueue(obj, p);
    }

    private void detach(Element element) {
        element.next.previous = element.previous;
        element.previous.next = element.next;
        element.next = element;
        element.previous = element;
    }

    public Object dequeue() {
        Element element = first();
        if (element == null) return null;
        detach(element);
        map.remove(element.obj);
        return element.obj;
    }

    public int size() {
        return map.size();
    }

    public Object peek() {
        Element element = first();
        if (element == null) return null;
        return element.obj;
    }

    private Element first() {
        Element element = head.previous;
        if (element == head) return null;
        return element;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[ ");
        Element element = head.previous;
        while (element != head) {
            sb.append(element.obj);
            element = element.previous;
            if (element != head) sb.append(", ");
        }
        sb.append(" ]");
        return sb.toString();
    }

}
