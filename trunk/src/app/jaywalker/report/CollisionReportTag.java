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

public class CollisionReportTag implements ReportTag {
    private final CollisionModel model;
    private final NestedReportTag[] nestedReportTags;
    private final ReportHelper reportHelper = new ReportHelper();

    public CollisionReportTag(CollisionModel model, NestedReportTag [] nestedReportTags ) {
        this.model = model;
        this.nestedReportTags = nestedReportTags;
    }

    public String create(URL url, Stack parentUrlStack) {
        URL [] collisionUrls = model.lookupCollisionUrls(url);
        if (collisionUrls == null) return "";
        boolean isConflict = isNestedElementsConflicting(collisionUrls);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < collisionUrls.length; i++) {
            sb.append(reportHelper.toSpaces(parentUrlStack.size()));
            sb.append("<collision url=\"").append(collisionUrls[i]).append("\"");
            if (!isConflict) {
                sb.append("/>\n");
            } else {
                sb.append(">\n");
                for (int j = 0; j < nestedReportTags.length; j++) {
                    sb.append(nestedReportTags[j].create(collisionUrls[i], parentUrlStack));
                }
                sb.append(reportHelper.toSpaces(parentUrlStack.size()));
                sb.append("</collision>\n");
            }
        }
        return sb.toString();

    }

    private boolean isNestedElementsConflicting(URL[] collisionUrls) {
        for ( int i = 0; i < nestedReportTags.length; i++) {
            if ( nestedReportTags[i].isNestedElementsConflicting(collisionUrls) ) {
                return true;
            }
        }
        return false;
    }

}
