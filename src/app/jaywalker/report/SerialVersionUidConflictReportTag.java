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

public class SerialVersionUidConflictReportTag implements NestedReportTag {
    private final CollisionModel model;
    private final ReportHelper reportHelper = new ReportHelper();

    public SerialVersionUidConflictReportTag(CollisionModel model) {
        this.model = model;
    }

    public String create(URL url, Stack parentUrlStack) {
        StringBuffer sb = new StringBuffer();
        long serialVersionUid = model.toSerialVersionUID(url);
        sb.append(reportHelper.toSpaces(parentUrlStack.size() + 1));
        sb.append("<conflict type=\"serialVersionUid\" value=\"");
        sb.append(serialVersionUid);
        sb.append("\"/>\n");
        return sb.toString();

    }

    public boolean isNestedElementsConflicting(URL [] urls) {
        return model.isSerialVersionUidsConflicting(urls);
    }
}
