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

import java.util.*;
import java.net.URL;

public class DependencyCycleFinder {

    private final static String YES = "yes";
    private final static String NO = "no";
    private final static String UNDEFINED = "undefined";

    private Map possibleCycleMap = new HashMap();
    private Map cycleUrlListMap = new HashMap();
    private DependencyCycleFinderHelper helper;

    public DependencyCycleFinder(DependencyCycleFinderHelper helper) {
        this.helper = helper;
    }

    public URL [] lookupCycleDependency(URL url) {
        Stack stack = new Stack();
        return lookupCycleDependency(url, stack);
    }

    private URL [] lookupCycleDependency(URL url, Stack stack) {
        Object isPartOfCycle = possibleCycleMap.get(url);
        if (isPartOfCycle == null) {
            resolveCycleDependencies(url, stack);
            isPartOfCycle = possibleCycleMap.get(url);
        }
        if (isPartOfCycle == YES) {
            List list = (List) cycleUrlListMap.get(url);
            return (URL[]) list.toArray(new URL[list.size()]);
        } else {
            return new URL[0];
        }
    }

    private void resolveCycleDependencies(URL url, Stack stack) {
        final URL[] resolvedDependencyUrls = helper.lookupResolvedDependencies(url);
        if (resolvedDependencyUrls == null || resolvedDependencyUrls.length == 0) {
            possibleCycleMap.put(url, NO);
            return;
        }

        for (int i = 0; i < resolvedDependencyUrls.length; i++) {
            Object isPartOfCycle = possibleCycleMap.get(resolvedDependencyUrls[i]);
            if (isPartOfCycle == null) {
                possibleCycleMap.put(resolvedDependencyUrls[i], UNDEFINED);
                stack.push(url);
                resolveCycleDependencies(resolvedDependencyUrls[i], stack);
                if (!stack.isEmpty()) stack.pop();
                isPartOfCycle = possibleCycleMap.get(resolvedDependencyUrls[i]);
            }
            if (isPartOfCycle == UNDEFINED && stack.size() > 0) {
                List cycleUrlList = new LinkedList();
                addCyclePart(url, cycleUrlList);
                URL cyclePartUrl;

                for (ListIterator it = stack.listIterator(stack.size()); it.hasPrevious();) {
                    URL nextUrl = (URL) it.previous();
                    if (!nextUrl.equals(url)) {
                        cyclePartUrl = nextUrl;
                        addCyclePart(cyclePartUrl, cycleUrlList);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private void addCyclePart(URL url, List cycleUrlList) {
        cycleUrlList.add(url);
        cycleUrlListMap.put(url, cycleUrlList);
        possibleCycleMap.put(url, YES);
    }

}
