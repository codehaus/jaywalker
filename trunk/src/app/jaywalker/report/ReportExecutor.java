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
import jaywalker.classlist.ClasslistElementVisitor;
import jaywalker.html.HtmlConfigVisitor;
import jaywalker.util.FileDecorator;
import jaywalker.util.FileDecoratorFactory;
import jaywalker.util.ResourceLocator;
import jaywalker.util.ZipExpander;
import jaywalker.xml.ConfigVisitor;

public class ReportExecutor {

	private ReportModelSetup reportModelSetup = new ReportModelSetup();

	private ClasslistElementFactory factory = new ClasslistElementFactory();

	private ReportEnvironment environment = new ReportEnvironment();

	public void execute(String classlist, Properties properties, File outDir,
			String tempPath) throws IOException {
		environment.initialize(classlist, properties, outDir, tempPath);
		final Report[] reports = reportModelSetup.toReports(properties);
		FileDecorator xmlFile = toFileDecorator(outDir, "report.xml");
		FileDecorator htmlFile = toFileDecorator(outDir, "report.html");
		outputXml(xmlFile, reports, classlist);
		outputHtml(htmlFile, xmlFile, "jaywalker-config.xml");
		extract("/META-INF/report.zip", outDir);
	}

	private void outputHtml(FileDecorator htmlFile, FileDecorator xmlFile,
			String configFilename) throws IOException {
		OutputStream os = htmlFile.getOutputStream();

		ConfigVisitor visitor = new HtmlConfigVisitor(os, xmlFile);
		visitor.setConfig("/META-INF/xml/" + configFilename);
		visitor.accept();

		os.flush();
		os.close();
	}

	private FileDecorator toFileDecorator(File outDir, String filename) {
		return new FileDecoratorFactory().create(outDir, filename);
	}

	private void extract(String filename, File outDir) {
		ZipInputStream zis = new ZipInputStream(FileDecorator.class
				.getResourceAsStream(filename));
		new ZipExpander(zis).expand(outDir);
	}

	private void outputXml(FileDecorator xmlFile, Report[] reports,
			String classlist) throws IOException {
		final ClasslistElement[] elements = factory.create(classlist);
		initialize(elements);
		outputXml(xmlFile, reports, elements);
	}

	private void outputXml(final FileDecorator reportFile, Report[] reports,
			final ClasslistElement[] elements) throws IOException {
		ClasslistElementVisitor visitor = new ClasslistElementVisitor(elements);
		Writer writer = reportFile.getWriter();

		AggregateReport report = new AggregateReport(reports, writer,
				lookupClasslistUrlsToIgnore());
		visitor.addListener(report);
		visitor.accept();

		writer.flush();
		writer.close();
	}

	private void initialize(final ClasslistElement[] elements)
			throws IOException {
		ClasslistElementVisitor visitor = new ClasslistElementVisitor(elements);
		initialize(visitor);
		visitor.removeAllListeners();
	}

	private void initialize(ClasslistElementVisitor visitor) throws IOException {
		AggregateModel model = new AggregateModel(reportModelSetup
				.getClasslistElementListeners());
		visitor.addListener(model);
		visitor.accept();
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
		return reportModelSetup.getReportDescriptions();
	}

}
