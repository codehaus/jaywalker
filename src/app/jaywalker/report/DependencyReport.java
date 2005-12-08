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
import java.util.Stack;

public class DependencyReport extends AbstractReport {
    private final DependencyModel model;
    private final ReportTag [] reportTags;

    public DependencyReport(final DependencyModel model, ReportTag [] reportTags) {
        this.model = model;
        this.reportTags = reportTags;
    }

    public String createElementSection(URL url, Stack parentUrlStack) {
        return createSection(reportTags, url, parentUrlStack);
    }

    public String createContainerSection(URL url, Stack parentUrlStack) {
        return createSection(reportTags, url, parentUrlStack);
    }

    private String createSection(ReportTag [] reportTags, URL url, Stack parentUrlStack) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < reportTags.length; i++) {
            sb.append(reportTags[i].create(url, parentUrlStack));
        }
        return sb.toString();
    }

    public ClasslistElementListener getModel() {
        return model;
    }
}
