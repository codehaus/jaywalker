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

import java.util.Properties;

import jaywalker.classlist.ClasslistElementListener;
import jaywalker.util.XsltTransformer;
import jaywalker.xml.ContainerCyclicDependencyTag;
import jaywalker.xml.ContainerDependencyTag;
import jaywalker.xml.ElementCyclicDependencyTag;
import jaywalker.xml.PackageCyclicDependencyTag;
import jaywalker.xml.PackageDependencyTag;
import jaywalker.xml.Tag;
import jaywalker.xml.UnresolvedClassNameDependencyTag;

public class DependencyReportConfiguration implements Configuration {
	private final ReportSetupMap dependencyReportTagMap;
	private final DependencyModel dependencyModel;

	public DependencyReportConfiguration(DependencyModel dependencyModel) {
		this.dependencyModel = dependencyModel;
		this.dependencyReportTagMap = createReportTagMap(dependencyModel);
	}

	private ReportSetupMap createReportTagMap(DependencyModel dependencyModel) {
		ReportSetupMap dependencyReportTagMap = new ReportSetupMap();
		dependencyReportTagMap.put("dependency", "archive",
				new ContainerDependencyTag(dependencyModel),
				new XsltTransformer("archive-dependencies-resolved-html.xslt"));
		dependencyReportTagMap.put("dependency", "package",
				new PackageDependencyTag(dependencyModel), new XsltTransformer(
						"package-dependencies-resolved-html.xslt"));
		dependencyReportTagMap.put("dependency", "class",
				new UnresolvedClassNameDependencyTag(dependencyModel),
				new XsltTransformer("class-dependencies-unresolved-html.xslt"));
		dependencyReportTagMap.put("cycle", "archive",
				new ContainerCyclicDependencyTag(dependencyModel),
				new XsltTransformer("archive-dependencies-cycle-html.xslt"));
		dependencyReportTagMap.put("cycle", "package",
				new PackageCyclicDependencyTag(dependencyModel), null);
		dependencyReportTagMap.put("cycle", "class",
				new ElementCyclicDependencyTag(dependencyModel), null);
		return dependencyReportTagMap;
	}
	
	public Tag[] toReportTags(Properties properties) {
		return dependencyReportTagMap.getXmlTags(properties);
	}

	public String[] getReportTypes() {
		return dependencyReportTagMap.getKeys();
	}

	public XsltTransformer[] toXsltTransformers(Properties properties) {
		return dependencyReportTagMap.getHtmlTransformers(properties);
	}

	public ClasslistElementListener getClasslistElementListener() {
		return dependencyModel;
	}

}
