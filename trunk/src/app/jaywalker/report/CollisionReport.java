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

public class CollisionReport extends AbstractReport {
    private final CollisionModel model;

    public CollisionReport(CollisionModel model) {
        this.model = model;
    }

    public String createContainerSection(URL url, Stack parentUrlStack) {
        return "";
    }

    public String createElementSection(URL url, Stack parentUrlStack) {
        URL [] collisionUrls = model.lookupCollisionUrls(url);

        if (collisionUrls == null) return "";

        boolean isConflict = model.isSerialVersionUidsConflicting(collisionUrls);

        StringBuffer sb = new StringBuffer();

        for (int k = 0; k < collisionUrls.length; k++) {
            sb.append(reportHelper.toSpaces(parentUrlStack.size()));
            sb.append("<collision url=\"").append(collisionUrls[k]).append("\"");
            if (!isConflict) {
                sb.append("/>\n");
            } else {
                sb.append(">\n");
                sb.append(createSerialVersionUidConflictTag(model.toSerialVersionUID(collisionUrls[k]), parentUrlStack));
                sb.append(reportHelper.toSpaces(parentUrlStack.size()));
                sb.append("</collision>\n");
            }
        }

        return sb.toString();

    }

    private String createSerialVersionUidConflictTag(long serialVersionUid, Stack parentUrlStack) {
        StringBuffer sb = new StringBuffer();
        sb.append(reportHelper.toSpaces(parentUrlStack.size() + 1));
        sb.append("<conflict type=\"serialVersionUid\" value=\"");
        sb.append(serialVersionUid);
        sb.append("\"/>\n");
        return sb.toString();
    }

    public ClasslistElementListener getModel() {
        return model;
    }
}
