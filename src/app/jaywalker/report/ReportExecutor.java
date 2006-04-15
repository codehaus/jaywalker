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
import jaywalker.util.Clock;
import jaywalker.util.ClockSubject;
import jaywalker.util.FileSystem;
import jaywalker.util.ResourceLocator;

public class ReportExecutor {

	private ConfigurationSetup configurationSetup = new ConfigurationSetup();

	private ClasslistElementFactory factory = new ClasslistElementFactory();

	public ReportExecutor() {
		String key = "javax.xml.transform.TransformerFactory";
		String value = "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";
		Properties props = System.getProperties();
		props.put(key, value);
		System.setProperties(props);
	}

	public void execute(final String classlist, Properties properties,
			File outDir) throws IOException {
		initOutDir(outDir);
		ResourceLocator.instance().register("clock", new Clock());
		final Report[] reports = configurationSetup.toReports(properties);
		final File output = new File(outDir, "report.xml");
		ResourceLocator.instance().register("report.xml", output);
		AggregateReport report = execute(classlist, reports, output);
		outputHtml(outDir, report, classlist);
	}

	// TODO: smells like an aspect
	private void outputHtml(File outDir, AggregateReport report,
			String classlist) throws IOException {
		Clock clock = (Clock) ResourceLocator.instance().lookup("clock");
		final String clockType = "Time to create html report";
		clock.start(clockType);
		try {
			File output = new File(outDir, "report.html");
			FileOutputStream fos = new FileOutputStream(output);
			fos.write(formatClasslist(classlist));
			report.transform(fos);
			fos.close();
		} finally {
			clock.stop(clockType);
			System.out.println(clock.toString(clockType));
		}

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

	// TODO: smells like an aspect
	private AggregateReport execute(String classlist, Report[] reports,
			File file) throws IOException {
		Clock clock = (Clock) ResourceLocator.instance().lookup("clock");
		final String clockType = "Time to execute report";
		clock.start(clockType);
		try {
			final ClasslistElement[] elements = factory.create(classlist);
			initReportModels(reports, elements);
			return createAggregateReport(reports, elements, file);
		} finally {
			clock.stop(clockType);
			System.out.println(clock.toString(clockType));
		}
	}

	private AggregateReport createAggregateReport(Report[] reports,
			final ClasslistElement[] elements, final File file)
			throws IOException {
		ClasslistElementVisitor visitor = new ClasslistElementVisitor(elements);
		FileWriter writer = new FileWriter(file);
		AggregateReport report = new AggregateReport(reports, writer);
		visitor.addListener(report);
		visitor.accept();
		writer.close();
		return report;
	}

	// TODO: smells like an aspect
	private void initReportModels(Report[] reports,
			final ClasslistElement[] elements) throws IOException {
		Clock clock = (Clock) ResourceLocator.instance().lookup("clock");
		final String clockType = "Time to initialize the report";
		clock.start(clockType);
		try {
			ClasslistElementVisitor visitor = new ClasslistElementVisitor(
					elements);
			final ClasslistElementStatistic statisticListener = new ClasslistElementStatistic();
			visitor.addListener(statisticListener);

			AggregateModel model = new AggregateModel(configurationSetup
					.getClasslistElementListeners());
			visitor.addListener(model);
			visitor.accept();

			System.out.println(statisticListener + " instrumented.");
			visitor.removeAllListeners();
		} finally {
			clock.stop(clockType);
			System.out.println(clock.toString(clockType));
		}
	}

	public String[] getReportDescriptions() {
		return configurationSetup.getReportDescriptions();
	}

}
