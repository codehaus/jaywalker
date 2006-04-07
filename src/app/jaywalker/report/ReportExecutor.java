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
import java.io.FileOutputStream;
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
		ResourceLocator.instance().register("report.xml", output);
		AggregateReport report = execute(classlist, reports, output);
		outputHtml(outDir, report, classlist);
	}

	private void outputHtml(File outDir, AggregateReport report, String classlist)
			throws IOException {
		File output = new File(outDir, "report.html");
		FileOutputStream fos = new FileOutputStream(output);
		fos.write(formatClasslist(classlist));
		report.transform(fos);
		fos.close();
	}

	private byte[] formatClasslist(String classlist) {
		final String html = "<b>Classlist:</b> " + classlist + "<br/>";
		return html.getBytes();
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
