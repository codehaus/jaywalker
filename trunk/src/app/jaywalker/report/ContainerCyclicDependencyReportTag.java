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

import jaywalker.util.URLHelper;

import java.net.URL;
import java.util.Stack;

public class ContainerCyclicDependencyReportTag implements ReportTag {
    private final DependencyModel model;
    private final ReportHelper reportHelper = new ReportHelper();

    public ContainerCyclicDependencyReportTag(DependencyModel model) {
        this.model = model;
    }

    public String create(URL url, Stack parentUrlStack) {
        URL [] containerCycleUrls = model.lookupContainerCyclicDependency(url);
        if (containerCycleUrls == null) return "";
        String [] values = new String[containerCycleUrls.length];
        for (int i = 0; i < containerCycleUrls.length; i++) {
            values[i] = new URLHelper().toFileName(containerCycleUrls[i]);
        }
        return reportHelper.createDependencyTags("cycle", "container", "archive", containerCycleUrls, values, parentUrlStack);
    }

}
