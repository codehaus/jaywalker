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
package jaywalker.xml;

import java.net.URL;
import java.util.Stack;

import jaywalker.report.DependencyModel;
import jaywalker.util.StringHelper;

public class PackageDependencyTag implements Tag {
	private final DependencyModel model;

	private final TagHelper reportHelper = new TagHelper();

	private final StringHelper stringHelper = new StringHelper();

	public PackageDependencyTag(DependencyModel model) {
		this.model = model;
	}

	public String create(URL url, Stack parentUrlStack) {
		String[] packageNames = model
				.lookupResolvedPackageNameDependencies(url);
		if (packageNames.length == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append(reportHelper.toSpaces(parentUrlStack.size()));
		sb.append("<dependency type=\"resolved\" value=\""
				+ packageNames.length + "\">\n");
		for (int i = 0; i < packageNames.length; i++) {
			sb.append(reportHelper.toSpaces(parentUrlStack.size() + 1));
			sb.append("<container type=\"package\" value=\"");
			sb.append(stringHelper.isEmpty(packageNames[i]) ? "default"
					: packageNames[i]);
			sb.append("\"/>\n");
		}
		sb.append(reportHelper.toSpaces(parentUrlStack.size()));
		sb.append("</dependency>\n");
		return sb.toString();
	}
}
