/**
 * Copyright 2005 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jaywalker.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementEvent;
import jaywalker.classlist.ClasslistElementFactory;
import jaywalker.classlist.ClasslistElementListener;
import jaywalker.classlist.JayWalkerTestCase;
import jaywalker.testutil.Path;
import jaywalker.util.Outputter;

public class AggregateReportTest extends JayWalkerTestCase {

	public AggregateReportTest(String name) {
		super(name);
	}

	private final ReportModel[] CONFIGURATIONS = new ReportModel[] {
			new DependencyReportModel(new DependencyModel()),
			new CollisionReportModel(new CollisionModel()) };

	protected ClasslistElementListener[] getClasslistElementListeners() {
		ClasslistElementListener[] listeners = new ClasslistElementListener[CONFIGURATIONS.length];
		for (int i = 0; i < CONFIGURATIONS.length; i++) {
			listeners[i] = CONFIGURATIONS[i].getClasslistElementListener();
		}
		return listeners;
	}

	public void assertCreateReportFor(URL url) throws IOException {
		Date start = new Date();

		Report[] reports = new Report[] { createCollisionReport(),
				createDependencyReport() };

		AggregateModel model = new AggregateModel(
				getClasslistElementListeners());
		assertVisit(url, model);
		BufferedWriter writer = new BufferedWriter(new StringWriter());
		AggregateReport report = new AggregateReport(reports, writer,
				new URL[0]);
		assertVisit(url, report);
		writer.close();
		System.out.println("Report initialization time : "
				+ (new Date().getTime() - start.getTime()));
		start = new Date();
		String reportValue = report.toString();
		System.out.println("Report creation time : "
				+ (new Date().getTime() - start.getTime()));
		System.out.println(reportValue);
	}

	public void testShouldIgnoreVisitingArchive() throws IOException,
			URISyntaxException {
		URL url = Path.FILE_TEST1_JAR.toURL();
		ClasslistElementListener listener = new ClasslistElementListener() {
			public void classlistElementVisited(ClasslistElementEvent event) {
				throw new IllegalStateException("should not be called");
			}

			public void lastClasslistElementVisited() {
			}
		};
		final ClasslistElement element = createClasslistElement(url);
		ClasslistElement[] elements = new ClasslistElement[] { element };
		AggregateModel visitor = new AggregateModel(
				getClasslistElementListeners());
		ClasslistElementEvent event = new ClasslistElementEvent(
				new ClasslistElementFactory().create(Path.FILE_TEST1_JAR
						.toURL()));
		visitor.classlistElementVisited(event);
	}

	private Report createDependencyReport() {
		Properties properties = new Properties();
		Tag[] reportTags = CONFIGURATIONS[0].toReportTags(properties);
		return new Report(reportTags);
	}

	private Report createCollisionReport() {
		Properties properties = new Properties();
		Tag[] reportTags = CONFIGURATIONS[1].toReportTags(properties);
		return new Report(reportTags);
	}

	public void testShouldCreateReportForAFile() throws IOException {
		assertCreateReportFor(Path.FILE_CLASSLIST_ELEMENT_FACTORY_CLASS.toURL());
	}

	public void testShouldCreateReportForFilesInADirectory() throws IOException {
		assertCreateReportFor(Path.DIR_BUILD_APP.toURL());
	}

	public void testShouldCreateReportForFilesInAnArchive() throws IOException {
		assertCreateReportFor(Path.FILE_TEST1_JAR.toURL());
	}

	public void testShouldCreateReportForFilesInNestedArchive()
			throws IOException {
		assertCreateReportFor(Path.FILE_TEST4_JAR.toURL());
	}

	public void testShouldCreateReportForFilesInNestedArchive2()
			throws IOException {
		assertCreateReportFor(Path.FILE_TEST5_JAR.toURL());
	}

	public void testShouldCreateReportForNestedArchivesInADirectory()
			throws IOException {
		assertCreateReportFor(Path.DIR_BUILD_JAR.toURL());
	}

	// public void testBiggie() throws IOException {
	// assertCreateReportFor(new File("C:\\temp\\weblogic").toURL());
	// }

}
