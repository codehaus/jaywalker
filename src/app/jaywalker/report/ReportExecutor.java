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
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.zip.ZipInputStream;

import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementFactory;
import jaywalker.classlist.ClasslistElementStatistic;
import jaywalker.classlist.ClasslistElementVisitor;
import jaywalker.html.ConfigVisitor;
import jaywalker.util.FileSystem;
import jaywalker.util.ResourceLocator;
import jaywalker.util.StringHelper;
import jaywalker.util.WriterOutputStream;
import jaywalker.util.ZipExpander;

public class ReportExecutor {

	private ConfigurationSetup configurationSetup = new ConfigurationSetup();

	private ClasslistElementFactory factory = new ClasslistElementFactory();

	private ResourceLocatorSetup locatorSetup = new ResourceLocatorSetup();

	public void execute(String classlist, Properties properties, File outDir,
			String tempPath) throws IOException {
		locatorSetup.registerWorkingDir(tempPath);
		locatorSetup.registerDeepClasslist(classlist);
		locatorSetup.register(properties);
		initializeDefaults(properties);
		printClasslist(classlist);
		initOutDir(outDir);
		final Report[] reports = configurationSetup.toReports(properties);
		ReportFile reportFile = new ReportFileFactory().create("report.xml");
		locatorSetup.register(reportFile);
		System.out.println("  Outputting report to "
				+ reportFile.getParentAbsolutePath());
		AggregateReport report = execute(classlist, reports, reportFile);
		outputHtml(outDir, report, classlist);
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

	private void outputHtml(File outDir, AggregateReport report,
			String classlist) throws IOException {
		ReportFile reportFile = new ReportFileFactory().create("report.html");
		OutputStream os = new WriterOutputStream(reportFile.getWriter());

		ConfigVisitor visitor = new ConfigVisitor(os);
		visitor.visit("jaywalker-config.xml");

		ZipInputStream zis = new ZipInputStream(ReportFile.class
				.getResourceAsStream("/META-INF/report.zip"));
		new ZipExpander(zis).expand(outDir);
	}

	private void initOutDir(File outDir) {
		if (outDir.exists()) {
			FileSystem.delete(outDir);
		}
		outDir.mkdir();
		locatorSetup.registerOutDir(outDir);
	}

	private AggregateReport execute(String classlist, Report[] reports,
			ReportFile reportFile) throws IOException {
		final ClasslistElement[] elements = factory.create(classlist);
		initReportModels(reports, elements);
		return createAggregateReport(reports, elements, reportFile);
	}

	private AggregateReport createAggregateReport(Report[] reports,
			final ClasslistElement[] elements, final ReportFile reportFile)
			throws IOException {
		ClasslistElementVisitor visitor = new ClasslistElementVisitor(elements);
		Writer writer = reportFile.getWriter();

		AggregateReport report = new AggregateReport(reports, writer,
				lookupClasslistUrlsToIgnore());
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
		visitor.accept();

		System.out.println("      " + statisticListener + " encountered.");
		visitor.removeAllListeners();
	}

	private URL[] lookupClasslistUrlsToIgnore() throws MalformedURLException {
		URL[] urls1 = lookupClasslistUrls("classlist-system");
		URL[] urls2 = lookupClasslistUrls("classlist-shallow");
		URL[] urls = new URL[urls1.length + urls2.length];
		System.arraycopy(urls1, 0, urls, 0, urls1.length);
		System.arraycopy(urls2, 0, urls, urls1.length, urls2.length);
		return urls;
	}

	private URL[] lookupClasslistUrls(String classlistType)
			throws MalformedURLException {
		if (ResourceLocator.instance().contains(classlistType)) {
			File[] files = (File[]) ResourceLocator.instance().lookup(
					classlistType);
			URL[] urls = new URL[files.length];
			for (int i = 0; i < files.length; i++) {
				urls[i] = files[i].toURL();
			}
			return urls;
		}
		return new URL[0];
	}

	public String[] getReportDescriptions() {
		return configurationSetup.getReportDescriptions();
	}

}
