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
import jaywalker.util.ChainedOutputter;
import jaywalker.util.DotOutputterFactory;
import jaywalker.util.Outputter;
import jaywalker.util.XsltTransformer;

public class DependencyReportConfiguration implements Configuration {
	private final ReportSetupMap dependencyReportTagMap;

	private final DependencyModel dependencyModel;

	public DependencyReportConfiguration(DependencyModel dependencyModel) {
		this.dependencyModel = dependencyModel;
		this.dependencyReportTagMap = createReportTagMap(dependencyModel);
	}

	private ReportSetupMap createReportTagMap(DependencyModel dependencyModel) {
		ReportSetupMap dependencyReportTagMap = new ReportSetupMap();

		Outputter[] archiveDependencyOutputters = new Outputter[] {
				new XsltTransformer("archive-dependencies-metrics-html.xslt"),
				new XsltTransformer("archive-dependencies-resolved-html.xslt"),
				new ChainedOutputter(new Outputter[] {
						new XsltTransformer(
								"archive-dependencies-resolved-dot.xslt"),
						new DotOutputterFactory()
								.create("archive.resolved.dot") }) };

		dependencyReportTagMap.put("dependency", "archive",
				new ContainerDependencyTag(dependencyModel),
				archiveDependencyOutputters);

		Outputter[] archiveCycleOutputters = createXsltChainedXsltDotOutputters(
				"archive-dependencies-cycle-html.xslt",
				"archive-dependencies-cycle-dot.xslt", "archive.cycle.dot");
		
		dependencyReportTagMap.put("cycle", "archive",
				new ContainerCyclicDependencyTag(dependencyModel),
				archiveCycleOutputters);
		
		Outputter[] packageDependencyOutputters = new Outputter[] {
				new XsltTransformer("package-dependencies-metrics-html.xslt"),
				new XsltTransformer("package-dependencies-resolved-html.xslt"),
				new ChainedOutputter(new Outputter[] {
						new XsltTransformer(
								"package-dependencies-resolved-dot.xslt"),
						new DotOutputterFactory()
								.create("package.resolved.dot") }) };

		dependencyReportTagMap.put("dependency", "package",
				new PackageDependencyTag(dependencyModel),
				packageDependencyOutputters);

		Outputter[] packageCycleOutputters = createXsltChainedXsltDotOutputters(
				"package-dependencies-cycle-html.xslt",
				"package-dependencies-cycle-dot.xslt", "package.cycle.dot");
		
		dependencyReportTagMap.put("cycle", "package",
				new PackageCyclicDependencyTag(dependencyModel),
				packageCycleOutputters);
		
		dependencyReportTagMap.put("dependency", "class",
				new UnresolvedClassNameDependencyTag(dependencyModel),
				new XsltTransformer("class-dependencies-unresolved-html.xslt"));

		Outputter[] classCycleOutputters = createXsltChainedXsltDotOutputters(
				"class-dependencies-cycle-html.xslt",
				"class-dependencies-cycle-dot.xslt", "class.cycle.dot");

		dependencyReportTagMap.put("cycle", "class",
				new ElementCyclicDependencyTag(dependencyModel),
				classCycleOutputters);

		return dependencyReportTagMap;
	}

	public Tag[] toReportTags(Properties properties) {
		return dependencyReportTagMap.getXmlTags(properties);
	}

	public String[] getReportTypes() {
		return dependencyReportTagMap.getKeys();
	}

	public Outputter[] toXsltTransformers(Properties properties) {
		return dependencyReportTagMap.getHtmlTransformers(properties);
	}

	public ClasslistElementListener getClasslistElementListener() {
		return dependencyModel;
	}

	private Outputter[] createXsltChainedXsltDotOutputters(String xsltFilename,
			String chainedXsltFilename, String chainedDotFilename) {
		return new Outputter[] {
				new XsltTransformer(xsltFilename),
				new ChainedOutputter(new Outputter[] {
						new XsltTransformer(chainedXsltFilename),
						new DotOutputterFactory().create(chainedDotFilename) }) };
	}

}
