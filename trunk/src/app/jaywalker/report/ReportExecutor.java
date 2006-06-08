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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementFactory;
import jaywalker.classlist.ClasslistElementStatistic;
import jaywalker.classlist.ClasslistElementVisitor;
import jaywalker.util.Clock;
import jaywalker.util.FileSystem;
import jaywalker.util.ResourceLocator;
import jaywalker.util.StringHelper;

public class ReportExecutor {

	private ConfigurationSetup configurationSetup = new ConfigurationSetup();

	private ClasslistElementFactory factory = new ClasslistElementFactory();

	public ReportExecutor() {
		ResourceLocator.instance().register("clock", new Clock());
	}

	private void setReportXmlResource(final ReportFile reportFile) {
		ResourceLocator.instance().register("report.xml",
				reportFile);
	}

	public void execute(String classlist, Properties properties, File outDir)
			throws IOException {
		initializeDefaults(properties);
		printClasslist(classlist);
		System.out.println("Creating the JayWalker report . . .");
		Clock clock = getClockResource();
		final String clockType = "Total time to create the JayWalker report";
		clock.start(clockType);
		try {
			initOutDir(outDir);
			final Report[] reports = configurationSetup.toReports(properties);
			ReportFile reportFile = new ReportFileFactory().create();
			setReportXmlResource(reportFile);
			System.out.println("  Outputting report to "
					+ reportFile.getParentAbsolutePath());
			AggregateReport report = execute(classlist, reports, reportFile);
			outputHtml(outDir, report, classlist);
		} finally {
			clock.stop(clockType);
			System.out.println(clock.toString(clockType));
		}

	}

	private void initializeDefaults(Properties properties) {
		setDefaultProperty(properties, "dependency", "archive,package,class");
		setDefaultProperty(properties, "collision", "class");
		setDefaultProperty(properties, "conflict", "class");
	}

	private void setDefaultProperty(Properties properties, String reportType,
			String defaultValue) {
		if (properties.getProperty(reportType) == null) {
			properties.setProperty(reportType, defaultValue);
		}
	}

	private void printClasslist(final String classlist) {
		System.out.print("Walking the classlist:\n");
		System.out.println(new StringHelper().spaceAndReplace(classlist, 2,
				File.pathSeparator, "\n"));
		System.out.println();
	}

	// TODO: smells like an aspect
	private void outputHtml(File outDir, AggregateReport report,
			String classlist) throws IOException {
		System.out.println("  Creating HTML report . . .");
		Clock clock = getClockResource();
		final String clockType = "    Time to create HTML report";
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
		ResourceLocator.instance().register("outDir", outDir);
	}

	// TODO: smells like an aspect
	private AggregateReport execute(String classlist, Report[] reports,
			ReportFile reportFile) throws IOException {
		System.out.println("  Creating XML report . . .");
		Clock clock = getClockResource();
		final String clockType = "    Total time to create XML report";
		clock.start(clockType);
		try {
			final ClasslistElement[] elements = factory.create(classlist);
			initReportModels(reports, elements);
			return createAggregateReport(reports, elements, reportFile);
		} finally {
			clock.stop(clockType);
			System.out.println(clock.toString(clockType));
		}
	}

	private AggregateReport createAggregateReport(Report[] reports,
			final ClasslistElement[] elements, final ReportFile reportFile)
			throws IOException {
		System.out.println("    Aggregating report elements . . .");
		Clock clock = getClockResource();
		final String clockType = "      Time to aggregate report elements";
		clock.start(clockType);
		try {
			ClasslistElementVisitor visitor = new ClasslistElementVisitor(
					elements);
			Writer writer = reportFile.getWriter();

			AggregateReport report = new AggregateReport(reports, writer);
			visitor.addListener(report);
			visitor.accept();

			writer.close();
			return report;
		} finally {
			clock.stop(clockType);
			System.out.println(clock.toString(clockType));
		}
	}

	private Clock getClockResource() {
		return (Clock) ResourceLocator.instance().lookup("clock");
	}

	// TODO: smells like an aspect
	private void initReportModels(Report[] reports,
			final ClasslistElement[] elements) throws IOException {
		System.out.println("    Initializing the report . . .");
		Clock clock = getClockResource();
		final String clockType = "      Time to initialize the report";
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

			System.out.println("      " + statisticListener + " encountered.");
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
