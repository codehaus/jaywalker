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

import jaywalker.classlist.ClassElement;
import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementEvent;
import jaywalker.classlist.ClasslistElementListener;

import java.net.URL;
import java.util.Map;
import java.util.Set;

public class DependencyModel implements ClasslistElementListener {
    private final DependencyHelper dependencyHelper = new DependencyHelper();
    private Map containerDependencyMap;
    private Map unresolvedMap;

    public void classlistElementVisited(ClasslistElementEvent event) {
        ClasslistElement classlistElement = event.getElement();
        if (classlistElement.getClass() == ClassElement.class) {
            ClassElement classElement = (ClassElement) classlistElement;
            URL url = classElement.getURL();
            String className = classElement.getName();
            dependencyHelper.markAsResolved(url, className);
            String [] dependencies = classElement.getDependencies();
            for (int i = 0; i < dependencies.length; i++) {
                String dependency = asClassName(dependencies[i]);
                if (!dependencyHelper.isResolved(dependency)) {
                    dependencyHelper.markAsUnresolved(url, dependency);
                } else {
                    dependencyHelper.updateResolved(url, dependency);
                }
            }
        }
    }

    public void lastClasslistElementVisited() {
        dependencyHelper.resolveSystemClasses();

        containerDependencyMap = dependencyHelper.getContainerDependencyMap();
        unresolvedMap = dependencyHelper.getUnresolvedDependencies();
    }

    private String asClassName(String dependency) {
        String value = dependency;
        int idx;
        if ((idx = value.lastIndexOf("[")) != -1)
            value = value.substring(idx + 2);
        if (value.endsWith(";"))
            value = value.substring(0, value.length() - 1);
        return value;
    }

    public Set lookupContainerDependencies(String urlString) {
        return (Set) containerDependencyMap.get(urlString);
    }

    public Set lookupUnresolvedElementDependencies(URL url) {
        return (Set) unresolvedMap.get(url);
    }

}
