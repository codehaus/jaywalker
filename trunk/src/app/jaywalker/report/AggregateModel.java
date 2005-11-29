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
import jaywalker.util.URLHelper;

import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AggregateModel implements ClasslistElementListener {
    private final DependencyHelper dependencyHelper = new DependencyHelper();
    private final CollisionHelper collisionHelper = new CollisionHelper();

    private Map urlCollisionMap;
    private Map containerDependencyMap;
    private SerialVersionUidHelper suidHelper;
    private Map unresolvedMap;

    public void classlistElementVisited(ClasslistElementEvent event) {
        ClasslistElement classlistElement = event.getElement();
        if (classlistElement.getClass() == ClassElement.class) {
            ClassElement classElement = (ClassElement) classlistElement;
            String className = classElement.getName();
            final URL url = classElement.getURL();

            collisionHelper.register(url, className);

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

    private String asClassName(String dependency) {
        String value = dependency;
        int idx;
        if ((idx = value.lastIndexOf("[")) != -1)
            value = value.substring(idx + 2);
        if (value.endsWith(";"))
            value = value.substring(0, value.length() - 1);
        return value;
    }

    public void lastClasslistElementVisited() {
        dependencyHelper.resolveSystemClasses();

        containerDependencyMap = dependencyHelper.getContainerDependencyMap();
        unresolvedMap = dependencyHelper.getUnresolvedDependencies();
        urlCollisionMap = collisionHelper.createUrlCollisionMap();
        suidHelper = new SerialVersionUidHelper(collisionHelper.createClassNameToUrlsMap());
    }

    public Set lookupContainerDependencies(String urlString) {
        return (Set) containerDependencyMap.get(urlString);
    }

    public URL[] lookupCollisionUrls(URL url) {
        return (URL[]) urlCollisionMap.get(url);
    }

    public Set lookupUnresolvedElementDependencies(URL url) {
        return (Set) unresolvedMap.get(url);
    }

    public boolean isSerialVersionUidsConflicting(URL[] collisionUrls) {
        return suidHelper.isSerialVersionUidsConflicting(collisionUrls);
    }

    public long toSerialVersionUID(URL collisionUrl) {
        return suidHelper.toSerialVersionUID(collisionUrl);
    }
}
