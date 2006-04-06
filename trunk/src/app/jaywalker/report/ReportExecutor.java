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
import java.util.Date;
import java.util.Properties;

import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementFactory;
import jaywalker.classlist.ClasslistElementStatistic;
import jaywalker.classlist.ClasslistElementVisitor;
import jaywalker.util.FileSystem;
import jaywalker.util.ResourceLocator;

public class ReportExecutor {

	private ConfigurationSetup configurationSetup = new ConfigurationSetup();

	private ClasslistElementFactory factory = new ClasslistElementFactory();

	public void execute(String classlist, Properties properties, File outDir)
			throws IOException {
		initOutDir(outDir);
		Report[] reports = configurationSetup.toReports(properties);
		File output = new File(outDir, "report.xml");
		AggregateReport report = execute(classlist, reports, output);

		outputXml(outDir, report, output);
		outputHtml(outDir, reports, classlist);
	}

	private void outputXml(File outDir, AggregateReport report, File output)
			throws IOException {
		ResourceLocator.instance().register("report.xml", output);
	}

	private void outputHtml(File outDir, Report[] reports, String classlist)
			throws IOException {
		File output = new File(outDir, "report.html");
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		writer.write(formatClasslist(classlist));
		for (int i = 0; i < reports.length; i++) {
			String[] strings = reports[i].transform();
			for (int j = 0; j < strings.length; j++) {
				writer.write(strings[j]);
			}
		}
		writer.close();
	}

	private String formatClasslist(String classlist) {
		return "<b>Classlist:</b> " + classlist + "<br/>";
	}

	private void initOutDir(File outDir) {
		if (outDir.exists()) {
			FileSystem.delete(outDir);
		}
		outDir.mkdir();
	}

	private AggregateReport execute(String classlist, Report[] reports,
			File file) throws IOException {
		final ClasslistElement[] elements = factory.create(classlist);
		initReportModels(reports, elements);
		return createAggregateReport(reports, elements, file);
	}

	private AggregateReport createAggregateReport(Report[] reports,
			final ClasslistElement[] elements, final File file)
			throws IOException {
		ClasslistElementVisitor visitor = new ClasslistElementVisitor(elements);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		AggregateReport report = new AggregateReport(reports, writer);
		visitor.addListener(report);
		visitor.accept();
		writer.close();
		return report;
	}

	private void initReportModels(Report[] reports,
			final ClasslistElement[] elements) throws IOException {
		ClasslistElementVisitor visitor = new ClasslistElementVisitor(elements);
		final ClasslistElementStatistic statisticListener = new ClasslistElementStatistic();
		visitor.addListener(statisticListener);

		AggregateModel model = new AggregateModel(configurationSetup
				.getClasslistElementListeners());
		visitor.addListener(model);
		Date start = new Date();
		visitor.accept();
		System.out.println("Time to visit elements : "
				+ (new Date().getTime() - start.getTime()));
		System.out.println(statisticListener + " instrumented.");
		visitor.removeAllListeners();
	}

	public String[] getReportDescriptions() {
		return configurationSetup.getReportDescriptions();
	}

}
