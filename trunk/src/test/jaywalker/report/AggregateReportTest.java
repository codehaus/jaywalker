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

import jaywalker.classlist.ClasslistElementListener;
import jaywalker.classlist.JayWalkerTestCase;
import jaywalker.testutil.Path;
import jaywalker.util.Outputter;
import jaywalker.xml.CollisionTag;
import jaywalker.xml.ContainerCyclicDependencyTag;
import jaywalker.xml.ContainerDependencyTag;
import jaywalker.xml.ElementCyclicDependencyTag;
import jaywalker.xml.NestedTag;
import jaywalker.xml.PackageCyclicDependencyTag;
import jaywalker.xml.SerialVersionUidConflictTag;
import jaywalker.xml.Tag;
import jaywalker.xml.UnresolvedClassNameDependencyTag;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

public class AggregateReportTest extends JayWalkerTestCase {
	
	private final Configuration[] CONFIGURATIONS = new Configuration[] {
			new DependencyReportConfiguration(new DependencyModel()),
			new CollisionReportConfiguration(new CollisionModel()) };

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

		AggregateModel model = new AggregateModel(getClasslistElementListeners());
		assertVisit(url, model);
		AggregateReport report = new AggregateReport(reports);
		assertVisit(url, report);
		System.out.println("Report initialization time : "
				+ (new Date().getTime() - start.getTime()));
		start = new Date();
		String reportValue = report.toString();
		System.out.println("Report creation time : "
				+ (new Date().getTime() - start.getTime()));
		System.out.println(reportValue);
	}

	private Report createDependencyReport() {
		Properties properties = new Properties();
		Tag[] reportTags = CONFIGURATIONS[0].toReportTags(properties);
		Outputter[] transformers = CONFIGURATIONS[0]
				.toXsltTransformers(properties);
		return new Report(reportTags, transformers);
	}

	private Report createCollisionReport() {
		Properties properties = new Properties();
		Tag[] reportTags = CONFIGURATIONS[1].toReportTags(properties);
		Outputter[] transformers = CONFIGURATIONS[1]
				.toXsltTransformers(properties);
		return new Report(reportTags, transformers);
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
