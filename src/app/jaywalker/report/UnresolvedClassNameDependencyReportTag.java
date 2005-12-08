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

public class UnresolvedClassNameDependencyReportTag implements ReportTag {
    private final DependencyModel model;
    private final ReportHelper reportHelper = new ReportHelper();

    public UnresolvedClassNameDependencyReportTag(DependencyModel model) {
        this.model = model;
    }

    public String create(URL url, Stack parentUrlStack) {
        String [] unresolvedClassNames = model.lookupUnresolvedElementDependencies(url);
        return createUnresolvedDependencyTags(unresolvedClassNames, parentUrlStack);
    }

    private String createUnresolvedDependencyTags(String []  unresolvedClassNames, Stack parentUrlStack) {
        if (unresolvedClassNames == null) return "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < unresolvedClassNames.length; i++) {
            sb.append(reportHelper.toSpaces(parentUrlStack.size()));
            sb.append("<dependency type=\"unresolved\" value=\"");
            sb.append(unresolvedClassNames[i]);
            sb.append("\"/>\n");
        }
        return sb.toString();
    }

}
