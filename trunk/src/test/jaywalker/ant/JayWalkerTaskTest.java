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

    public void testUnifiedReportForClassElement() {
        executeTarget("testUnifiedReportForClassElement");
    }

    public void testUnifiedReportForClassesDirectory() {
        executeTarget("testUnifiedReportForClassesDirectory");
    }

    public void testUnifiedReportForTest1Archive() {
        executeTarget("testUnifiedReportForTest1Archive");
    }

    public void testUnifiedReportForTest2Archive() {
        executeTarget("testUnifiedReportForTest2Archive");
    }
    
    public void testUnifiedReportForTest1Test2Archive() {
        executeTarget("testUnifiedReportForTest1Test2Archive");
    }

    public void testUnifiedReportForTest4Archive() {
        executeTarget("testUnifiedReportForTest4Archive");
    }

    public void testUnifiedReportForEarArchive() {
        executeTarget("testUnifiedReportForEarArchive");
    }
    
    public void testUnifiedReportForBuiltArchives() {
        executeTarget("testUnifiedReportForBuiltArchives");
    }

    public void testUnifiedReportForCycle1AndCycle2Archive() {
        executeTarget("testUnifiedReportForCycle1AndCycle2Archive");
    }

}
