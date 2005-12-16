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

	private NestedReportTag[] nestedReportTags = new NestedReportTag[0];

	private final ReportHelper reportHelper = new ReportHelper();

	public CollisionReportTag(CollisionModel model) {
		this.model = model;
	}

	public String create(URL url, Stack parentUrlStack) {
		URL[] collisionUrls = model.lookupCollisionUrls(url);
		if (collisionUrls == null)
			return "";
		boolean isConflict = isConflicting(url, collisionUrls);
		StringBuffer sb = new StringBuffer();
		if (isConflict) {
			for (int j = 0; j < nestedReportTags.length; j++) {
				sb.append(nestedReportTags[j].create(url, parentUrlStack));
			}
		}
		for (int i = 0; i < collisionUrls.length; i++) {
			sb.append(reportHelper.toSpaces(parentUrlStack.size()));
			sb.append("<collision url=\"").append(collisionUrls[i])
					.append("\"");
			if (!isConflict) {
				sb.append("/>\n");
			} else {
				sb.append(">\n");
				for (int j = 0; j < nestedReportTags.length; j++) {
					sb.append(nestedReportTags[j].create(collisionUrls[i],
							parentUrlStack));
				}
				sb.append(reportHelper.toSpaces(parentUrlStack.size()));
				sb.append("</collision>\n");
			}
		}
		return sb.toString();

	}

	private boolean isConflicting(URL url, URL[] collisionUrls) {
		boolean isConflict;
		URL[] urls = new URL[collisionUrls.length + 1];
		urls[0] = url;
		System.arraycopy(collisionUrls, 0, urls, 1, collisionUrls.length);
		isConflict = isNestedElementsConflicting(urls);
		return isConflict;
	}

	public void setNestedReportTags(NestedReportTag[] nestedReportTags) {
		this.nestedReportTags = nestedReportTags;
	}

	private boolean isNestedElementsConflicting(URL[] collisionUrls) {
		for (int i = 0; i < nestedReportTags.length; i++) {
			if (nestedReportTags[i].isNestedElementsConflicting(collisionUrls)) {
				return true;
			}
		}
		return false;
	}

}
