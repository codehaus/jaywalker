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
package jaywalker.xml;

import jaywalker.classlist.ClassElement;
import jaywalker.classlist.ClasslistElementFactory;
import jaywalker.report.DependencyModel;

import java.net.URL;
import java.util.Stack;

public class ElementCyclicDependencyTag implements Tag {
    private final DependencyModel model;
    private final ClasslistElementFactory factory = new ClasslistElementFactory();
    private final TagHelper reportHelper = new TagHelper();

    public ElementCyclicDependencyTag(DependencyModel model) {
        this.model = model;
    }

    public String create(URL url, Stack parentUrlStack) {
        URL [] cycleUrls = model.lookupElementCycleDependency(url);
        String[] values = toValues(cycleUrls);
        return reportHelper.createDependencyTags("cycle", "element", "class", cycleUrls, values, parentUrlStack);
    }

    private String[] toValues(URL[] cycleUrls) {
        String [] values = new String[cycleUrls.length];
        for (int i = 0; i < cycleUrls.length; i++) {
            ClassElement classElement = (ClassElement) factory.create(cycleUrls[i]);
            values[i] = classElement.getName();
        }
        return values;
    }
}
