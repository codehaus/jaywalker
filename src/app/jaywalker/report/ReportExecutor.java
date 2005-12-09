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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementFactory;
import jaywalker.classlist.ClasslistElementStatistic;
import jaywalker.classlist.ClasslistElementVisitor;

public class ReportExecutor {
	final DependencyModel dependencyModel;

	final DependencyReportConfiguration dependencyReportConfiguration;

	final CollisionModel collisionModel;

	final CollisionReportConfiguration collisionReportConfiguration;

	public ReportExecutor() {
		dependencyModel = new DependencyModel();
		dependencyReportConfiguration = new DependencyReportConfiguration(
				dependencyModel);
		collisionModel = new CollisionModel();
		collisionReportConfiguration = new CollisionReportConfiguration(
				collisionModel);
	}

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
		ReportTag[] dependencyReportTags = dependencyReportConfiguration
				.toReportTags(properties);

		ReportTag[] collisionReportTags = collisionReportConfiguration
				.toReportTags(properties);

		Report[] reports = new Report[] {
				new CollisionReport(collisionModel, collisionReportTags),
				new DependencyReport(dependencyModel, dependencyReportTags) };
		return reports;
	}

	public String[] getReportDescriptions() {
		String [] reportTypes = getReportTypes();
		Map map = toReportTypeMap(reportTypes);
		Set keySet = map.keySet();
		List descriptionList = new ArrayList();
		for (Iterator itKey = keySet.iterator(); itKey.hasNext();) {
			final Object key = itKey.next();
			Set valueSet = (Set) map.get(key);
			StringBuffer sb = new StringBuffer();
			sb.append(key).append("=");
			for (Iterator itValue = valueSet.iterator(); itValue.hasNext();) {
				sb.append(itValue.next());
				if (itValue.hasNext())
					sb.append(",");
			}
			descriptionList.add(sb.toString());
		}
		return (String[]) descriptionList.toArray(new String[descriptionList
				.size()]);
	}

	private String [] getReportTypes() {
		List reportTypeList = new ArrayList();
		reportTypeList.addAll(Arrays.asList(dependencyReportConfiguration
				.getReportTypes()));
		reportTypeList.addAll(Arrays.asList(collisionReportConfiguration
				.getReportTypes()));
		return (String[]) reportTypeList.toArray(new String[reportTypeList.size()]);
	}

	private Map toReportTypeMap(String [] reportTypes) {
		Map map = new HashMap();
		for (int i = 0; i < reportTypes.length;i++) {
			String reportType = reportTypes[i];
			String[] reporTypeAttributes = reportType.split(",");
			Set set = (Set) map.get(reporTypeAttributes[0]);
			if (set == null) {
				set = new HashSet();
				map.put(reporTypeAttributes[0], set);
			}
			set.add(reporTypeAttributes[1]);
		}
		return map;
	}

}
