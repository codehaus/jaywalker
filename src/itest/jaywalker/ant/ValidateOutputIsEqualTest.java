package jaywalker.ant;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileTest;
import org.custommonkey.xmlunit.XMLTestCase;
import org.xml.sax.SAXException;

public class ValidateOutputIsEqualTest extends BuildFileTest {
	public ValidateOutputIsEqualTest(String name) {
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

	private void assertXMLEquals(String testCase) throws FileNotFoundException,
			SAXException, IOException, ParserConfigurationException {
		new XMLTestCase().assertXMLEqual(
				"Output for Ant and Command line should be the same",
				new FileReader("build/temp/ant/JayWalkerTaskTest." + testCase
						+ "/report.xml"), new FileReader(
						"build/temp/cmdline/JayWalkerTaskTest." + testCase
								+ "/report.xml"));
	}

	private void executeAndAssertXMLEquals(String testCase)
			throws FileNotFoundException, SAXException, IOException,
			ParserConfigurationException {
		executeTarget(testCase);
		assertXMLEquals(testCase);
	}

	public void testUnifiedReportForClassElement()
			throws FileNotFoundException, SAXException, IOException,
			ParserConfigurationException {
		executeAndAssertXMLEquals("testUnifiedReportForClassElement");
	}

	public void testUnifiedReportForClassesDirectory()
			throws FileNotFoundException, SAXException, IOException,
			ParserConfigurationException {
		executeAndAssertXMLEquals("testUnifiedReportForClassesDirectory");
	}

	public void testUnifiedReportForTest1Archive()
			throws FileNotFoundException, SAXException, IOException,
			ParserConfigurationException {
		executeAndAssertXMLEquals("testUnifiedReportForTest1Archive");
	}

	public void testUnifiedReportForTest2Archive()
			throws FileNotFoundException, SAXException, IOException,
			ParserConfigurationException {
		executeAndAssertXMLEquals("testUnifiedReportForTest2Archive");
	}

	public void testUnifiedReportForTest1Test2Archive()
			throws FileNotFoundException, SAXException, IOException,
			ParserConfigurationException {
		executeAndAssertXMLEquals("testUnifiedReportForTest1Test2Archive");
	}

	public void testUnifiedReportForTest4Archive()
			throws FileNotFoundException, SAXException, IOException,
			ParserConfigurationException {
		executeAndAssertXMLEquals("testUnifiedReportForTest4Archive");
	}

	public void testUnifiedReportForEarArchive() throws FileNotFoundException,
			SAXException, IOException, ParserConfigurationException {
		executeAndAssertXMLEquals("testUnifiedReportForEarArchive");
	}

	public void testUnifiedReportForBuiltArchives()
			throws FileNotFoundException, SAXException, IOException,
			ParserConfigurationException {
		executeAndAssertXMLEquals("testUnifiedReportForBuiltArchives");
	}

	public void testUnifiedReportForCycle1AndCycle2Archive()
			throws FileNotFoundException, SAXException, IOException,
			ParserConfigurationException {
		executeAndAssertXMLEquals("testUnifiedReportForCycle1AndCycle2Archive");
	}

	public void testUnifiedReportForJayWalkerProject()
			throws FileNotFoundException, SAXException, IOException,
			ParserConfigurationException {
		executeAndAssertXMLEquals("testUnifiedReportForJayWalkerProject");
	}

}
