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

import jaywalker.classlist.ClasslistElementListener;

import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class DependencyReport extends AbstractReport {
    private final DependencyModel model;

    public DependencyReport(DependencyModel model) {
        this.model = model;
    }

    public String createElementSection(URL url, Stack parentUrlStack) {
        Set unresolvedUrlSet = model.lookupUnresolvedElementDependencies(url);
        if (unresolvedUrlSet == null) return "";
        return createUnresolvedDependencyTags(unresolvedUrlSet, parentUrlStack);
    }

    public String createContainerSection(URL url, Stack parentUrlStack) {
        String urlString = url.toString();
        Set containerDependencySet = model.lookupContainerDependencies(urlString);
        return createResolvedContainerDependenciesTag(containerDependencySet, parentUrlStack);
    }

    private String createResolvedContainerDependenciesTag(Set containerDependencySet, Stack parentUrlStack) {
        StringBuffer sb = new StringBuffer();
        if (containerDependencySet != null) {
            for (Iterator itContainerDependency = containerDependencySet.iterator(); itContainerDependency.hasNext();) {
                sb.append(reportHelper.toSpaces(parentUrlStack.size()));
                sb.append("<dependency type=\"resolved\" value=\"").append(itContainerDependency.next());
                sb.append("\"/>\n");
            }
        }
        return sb.toString();
    }

    private String createUnresolvedDependencyTags(Set unresolvedSet, Stack parentUrlStack) {
        StringBuffer sb = new StringBuffer();
        for (Iterator it2 = unresolvedSet.iterator(); it2.hasNext();) {
            sb.append(reportHelper.toSpaces(parentUrlStack.size()));
            sb.append("<dependency type=\"unresolved\" value=\"");
            sb.append(it2.next());
            sb.append("\"/>\n");
        }
        return sb.toString();
    }


    public ClasslistElementListener getModel() {
        return model;
    }
}
