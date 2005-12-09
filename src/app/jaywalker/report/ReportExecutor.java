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

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Properties;

import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementFactory;
import jaywalker.classlist.ClasslistElementStatistic;
import jaywalker.classlist.ClasslistElementVisitor;

public class ReportExecutor {
	public void execute(String classlist, Properties properties, Writer writer)
			throws IOException {
		final ClasslistElementFactory factory = new ClasslistElementFactory();
		final ClasslistElement[] elements = factory.create(classlist);
		ClasslistElementVisitor visitor = new ClasslistElementVisitor(elements);
		final ClasslistElementStatistic statisticListener = new ClasslistElementStatistic();
		visitor.addListener(statisticListener);

		Report[] reports = createReports(properties);

		AggregateModel model = new AggregateModel(reports);
		visitor.addListener(model);
		Date start = new Date();
		visitor.accept();
		System.out.println("Time to visit elements : "
				+ (new Date().getTime() - start.getTime()));
		System.out.println(statisticListener + " instrumented.");
		visitor.removeAllListeners();
		AggregateReport report = new AggregateReport(reports);
		visitor.addListener(report);
		visitor.accept();

		writer.write(report.toString());
		writer.close();
	}

	private Report[] createReports(Properties properties) {
		final DependencyModel dependencyModel = new DependencyModel();
		DependencyReportConfiguration dependencyReportConfiguration = new DependencyReportConfiguration(
				dependencyModel);
		ReportTag[] dependencyReportTags = dependencyReportConfiguration
				.toReportTags(properties);

		CollisionModel collisionModel = new CollisionModel();
		CollisionReportConfiguration collisionReportConfiguration = new CollisionReportConfiguration(
				collisionModel);
		ReportTag[] collisionReportTags = collisionReportConfiguration
				.toReportTags(properties);

		Report[] reports = new Report[] {
				new CollisionReport(collisionModel, collisionReportTags),
				new DependencyReport(dependencyModel, dependencyReportTags) };
		return reports;
	}

}
