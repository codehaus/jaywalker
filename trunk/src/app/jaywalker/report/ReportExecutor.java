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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementFactory;
import jaywalker.classlist.ClasslistElementListener;
import jaywalker.classlist.ClasslistElementStatistic;
import jaywalker.classlist.ClasslistElementVisitor;
import jaywalker.util.FileSystem;
import jaywalker.util.ResourceLocator;
import jaywalker.util.XsltTransformer;
import jaywalker.xml.Tag;

public class ReportExecutor {

	private final Configuration[] CONFIGURATIONS = new Configuration[] {
		new DependencyReportConfiguration(new DependencyModel()),
		new CollisionReportConfiguration(new CollisionModel()) };
	
	private ClasslistElementFactory factory = new ClasslistElementFactory();

	public void execute(String classlist, Properties properties, File outDir)
			throws IOException {
		initOutDir(outDir);
		Report[] reports = valueOf(properties);
		AggregateReport report = execute(classlist, reports);

		outputXml(outDir, report);
		outputHtml(outDir, reports);
	}
	
	private String[] getReportTypes() {
		List reportTypeList = new ArrayList();
		for (int i = 0; i < CONFIGURATIONS.length; i++) {
			reportTypeList.addAll(Arrays.asList(CONFIGURATIONS[i]
					.getReportTypes()));
		}
		return (String[]) reportTypeList.toArray(new String[reportTypeList
				.size()]);
	}

	private Report[] valueOf(Properties properties) {
		List reportList = new LinkedList();
		for (int i = 0; i < CONFIGURATIONS.length; i++) {
			Tag[] reportTags = CONFIGURATIONS[i].toReportTags(properties);
			if (reportTags.length > 0) {
				XsltTransformer[] transformers = CONFIGURATIONS[i]
						.toXsltTransformers(properties);
				reportList.add(new Report(reportTags, transformers));
			}
		}
		return (Report[]) reportList.toArray(new Report[reportList.size()]);
	}

	private void outputXml(File outDir, AggregateReport report)
			throws IOException {
		File output = new File(outDir, "report.xml");
		ResourceLocator.instance().register("report.xml", output);
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		writer.write(report.toString());
		writer.close();
	}

	private void outputHtml(File outDir, Report[] reports) throws IOException {
		File output = new File(outDir, "report.html");
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		for (int i = 0; i < reports.length; i++) {
			String[] strings = reports[i].transform();
			for (int j = 0; j < strings.length; j++) {
				writer.write(strings[j]);
			}
		}
		writer.close();
	}

	private void initOutDir(File outDir) {
		if (outDir.exists()) {
			FileSystem.delete(outDir);
		}
		outDir.mkdir();
	}

	private AggregateReport execute(String classlist, Report[] reports)
			throws IOException {
		final ClasslistElement[] elements = factory.create(classlist);
		initReportModels(reports, elements);

		ClasslistElementVisitor visitor = new ClasslistElementVisitor(elements);
		AggregateReport report = new AggregateReport(reports);
		visitor.addListener(report);
		visitor.accept();
		return report;
	}

	public ClasslistElementListener[] getClasslistElementListeners() {
		ClasslistElementListener[] listeners = new ClasslistElementListener[CONFIGURATIONS.length];
		for (int i = 0; i < CONFIGURATIONS.length; i++) {
			listeners[i] = CONFIGURATIONS[i].getClasslistElementListener();
		}
		return listeners;
	}
	
	private void initReportModels(Report[] reports,
			final ClasslistElement[] elements) throws IOException {
		ClasslistElementVisitor visitor = new ClasslistElementVisitor(elements);
		final ClasslistElementStatistic statisticListener = new ClasslistElementStatistic();
		visitor.addListener(statisticListener);
		
		AggregateModel model = new AggregateModel(getClasslistElementListeners());
		visitor.addListener(model);
		Date start = new Date();
		visitor.accept();
		System.out.println("Time to visit elements : "
				+ (new Date().getTime() - start.getTime()));
		System.out.println(statisticListener + " instrumented.");
		visitor.removeAllListeners();
	}

	public String[] getReportDescriptions() {
		String[] reportTypes = getReportTypes();
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

	private Map toReportTypeMap(String[] reportTypes) {
		Map map = new HashMap();
		for (int i = 0; i < reportTypes.length; i++) {
			String reportType = reportTypes[i];
			String[] reportTypeAttributes = reportType.split(",");
			Set set = (Set) map.get(reportTypeAttributes[0]);
			if (set == null) {
				set = new HashSet();
				map.put(reportTypeAttributes[0], set);
			}
			set.add(reportTypeAttributes[1]);
		}
		return map;
	}

}
