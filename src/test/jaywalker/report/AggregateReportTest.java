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

import jaywalker.classlist.JayWalkerTestCase;
import jaywalker.testutil.Path;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

public class AggregateReportTest extends JayWalkerTestCase {
    public void assertCreateReportFor(URL url) throws IOException {
        Date start = new Date();
        AggregateReport report = new AggregateReport();
        assertVisit(url, report);
        System.out.println("Report initialization time : " + (new Date().getTime() - start.getTime()));
        start = new Date();
        String reportValue = report.toString();
        System.out.println("Report creation time : " + (new Date().getTime() - start.getTime()));
        System.out.println(reportValue);
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

    public void testShouldCreateReportForFilesInNestedArchive() throws IOException {
        assertCreateReportFor(Path.FILE_TEST4_JAR.toURL());
    }

    public void testShouldCreateReportForFilesInNestedArchive2() throws IOException {
        assertCreateReportFor(Path.FILE_TEST5_JAR.toURL());
    }

    public void testShouldCreateReportForNestedArchivesInADirectory() throws IOException {
        assertCreateReportFor(Path.DIR_BUILD_JAR.toURL());
    }

//    public void testBiggie() throws IOException {
//        assertCreateReportFor(new File("C:\\temp\\weblogic").toURL());
//    }


}
