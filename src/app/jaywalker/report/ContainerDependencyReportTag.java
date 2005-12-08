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

public class ContainerDependencyReportTag implements ReportTag {
    private final DependencyModel model;
    private final ReportHelper reportHelper = new ReportHelper();

    public ContainerDependencyReportTag(DependencyModel model) {
        this.model = model;
    }

    public String create(URL url, Stack parentUrlStack) {
        URL [] containerDependencyUrls = model.lookupResolvedContainerDependencies(url);
        if (containerDependencyUrls == null) return "";
        String[] values = toFileNames(containerDependencyUrls);
        return reportHelper.createDependencyTags("resolved", "container", "archive", containerDependencyUrls, values, parentUrlStack);
    }

    private String[] toFileNames(URL[] urls) {
        String [] values = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            values[i] = new URLHelper().toFileName(urls[i]);
        }
        return values;
    }

}
