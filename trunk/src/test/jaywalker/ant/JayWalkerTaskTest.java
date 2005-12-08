package jaywalker.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileTest;

public class JayWalkerTaskTest extends BuildFileTest {
    public JayWalkerTaskTest(String name) {
        super(name);
    }

    public void setUp() {
        configureProject("build-ant-test.xml");
    }

    public void testTaskOutputNotPresent() {
        try {
            executeTarget("testTaskOutputNotPresent");
            fail("BuildException was not thrown.");
        } catch (BuildException e) {
            assertNotNull(e);
        }
    }

    public void testTaskClasslistNotPresent() {
        try {
            executeTarget("testTaskClasslistNotPresent");
            fail("BuildException was not thrown.");
        } catch (BuildException e) {
            assertNotNull(e);
        }
    }

    public void testReportAggregateForClassElement() {
        executeTarget("testReportAggregateForClassElement");
    }

    public void testReportAggregateForClassesDirectory() {
        executeTarget("testReportAggregateForClassesDirectory");
    }

    public void testReportAggregateForTest1Archive() {
        executeTarget("testReportAggregateForTest1Archive");
    }

    public void testReportAggregateForTest2Archive() {
        executeTarget("testReportAggregateForTest2Archive");
    }

    public void testReportAggregateForTest4Archive() {
        executeTarget("testReportAggregateForTest4Archive");
    }

    public void testReportAggregateForBuiltArchives() {
        executeTarget("testReportAggregateForBuiltArchives");
    }

    public void testReportAggregateForCycle1AndCycle2Archive() {
        executeTarget("testReportAggregateForCycle1AndCycle2Archive");
    }


}
