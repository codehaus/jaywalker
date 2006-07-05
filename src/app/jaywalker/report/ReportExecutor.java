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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementFactory;
import jaywalker.classlist.ClasslistElementStatistic;
import jaywalker.classlist.ClasslistElementVisitor;
import jaywalker.util.Clock;
import jaywalker.util.FileSystem;
import jaywalker.util.ResourceLocator;
import jaywalker.util.Shell;
import jaywalker.util.StringHelper;
import jaywalker.util.WriterOutputStream;

public class ReportExecutor {

	private ConfigurationSetup configurationSetup = new ConfigurationSetup();

	private ClasslistElementFactory factory = new ClasslistElementFactory();

	public ReportExecutor() {
		ResourceLocator.instance().register("clock", new Clock());
	}

	private void setReportXmlResource(final ReportFile reportFile) {
		ResourceLocator.instance().register("report.xml", reportFile);
	}

	private void registerWorkingDir(String tempPath) throws IOException {
		File workingDir = Shell.toWorkingDir(tempPath);
		ResourceLocator.instance().register("tempDir", workingDir);
	}

	private void registerClasslist(Properties properties, String classlistType) {
		if (properties.containsKey(classlistType)) {
			String classpath = properties.getProperty(classlistType);
			String[] classpaths = classpath.split(File.pathSeparator);
			File[] files = new File[classpaths.length];
			for (int i = 0; i < classpaths.length; i++) {
				files[i] = new File(classpaths[i]);
			}
			ResourceLocator.instance().register(classlistType, files);
		}
	}

	public void execute(String classlist, Properties properties, File outDir,
			String tempPath) throws IOException {
		registerWorkingDir(tempPath);
		registerClasslist(properties, "classlist-shallow");
		registerClasslist(properties, "classlist-system");
		registerIncludeJayWalkerJarFile(properties);
		initializeDefaults(properties);
		printClasslist(classlist);
		System.out.println("Creating the JayWalker report . . .");
		Clock clock = getClockResource();
		final String clockType = "Total time to create the JayWalker report";
		clock.start(clockType);
		try {
			initOutDir(outDir);
			final Report[] reports = configurationSetup.toReports(properties);
			ReportFile reportFile = new ReportFileFactory()
					.create("report.xml");
			setReportXmlResource(reportFile);
			System.out.println("  Outputting report to "
					+ reportFile.getParentAbsolutePath());
			AggregateReport report = execute(classlist, reports, reportFile);
			outputHtml(outDir, report, classlist);
			outputStyleSheet(outDir);
		} finally {
			clock.stop(clockType);
			System.out.println(clock.toString(clockType));
		}

	}

	private void registerIncludeJayWalkerJarFile(Properties properties) {
		if (properties.containsKey("includeJayWalkerJarFile")) {
			Boolean includeJayWalkerJarFile = Boolean.valueOf(properties
					.getProperty("includeJayWalkerJarFile"));
			ResourceLocator.instance().register("includeJayWalkerJarFile",
					includeJayWalkerJarFile);
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
			ReportFile reportFile = new ReportFileFactory()
					.create("report.html");
			OutputStream os = new WriterOutputStream(reportFile.getWriter());
			os.write(createTitle("JayWalker Report"));
			os.write(createStylesheetLink("stylesheet.css"));
			os.write(createClasslistTable(classlist,
					lookupClasslist("classlist-shallow"),
					lookupClasslist("classlist-system")));
			report.transform(os);

			os.flush();
			os.close();
		} finally {
			clock.stop(clockType);
			System.out.println(clock.toString(clockType));
		}
	}

	private byte[] createClasslistTable(String deepValue, String shallowValue,
			String systemValue) {
		StringBuffer sb = new StringBuffer();
		sb.append("<h3>Classlists</h3>");
		sb.append("<table width=\"95%\" cellspacing=\"2\" ");
		sb.append("cellpadding=\"5\" border=\"0\" class=\"details\">");
		sb.append("<tr><th>Classlist</th><th>Value</th></tr>");
		sb.append(createKeyValueTableRow("default (deep)", deepValue));
		sb.append(createKeyValueTableRow("shallow", shallowValue));
		sb.append(createKeyValueTableRow("system", systemValue));
		sb.append("</table>");
		return sb.toString().getBytes();
	}

	private String createKeyValueTableRow(String key, String value) {
		StringBuffer sb = new StringBuffer();
		sb.append("<tr><td>");
		sb.append(key);
		sb.append("</td><td>");
		sb.append(value);
		sb.append("</td></tr>");
		return sb.toString();
	}

	private byte[] createTitle(String value) {
		String html = "<h1>" + value + "</h1>";
		return html.getBytes();
	}

	private void outputStyleSheet(File outDir) {
		File file = new File(outDir, "stylesheet.css");
		InputStream is = ReportExecutor.class
				.getResourceAsStream("/META-INF/css/stylesheet.css");
		try {
			FileOutputStream fos = new FileOutputStream(file);

			int i = is.read();
			while (i != -1) {
				fos.write(i);
				i = is.read();
			}
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] createStylesheetLink(String filename) {
		String html = "<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\""
				+ filename + "\">\n";
		return html.getBytes();
	}

	private String lookupClasslist(String classlistType) {
		StringBuffer sb = new StringBuffer();
		if (ResourceLocator.instance().contains(classlistType)) {
			File[] files = (File[]) ResourceLocator.instance().lookup(
					classlistType);
			for (int i = 0; i < files.length; i++) {
				sb.append(files[i].getAbsoluteFile());
				if (i + 1 < files.length) {
					sb.append(File.pathSeparator);
				}
			}
		}
		return sb.toString();
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

			AggregateReport report = new AggregateReport(reports, writer,
					lookupClasslistUrlsToIgnore());
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
