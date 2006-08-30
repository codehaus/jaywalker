package jaywalker.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import jaywalker.testutil.Path;
import junit.framework.TestCase;

import org.xml.sax.SAXException;

public class ConfigParserTest extends TestCase {

	private String configPath = Path.FILE_REPORT_CONFIG_XML.getAbsolutePath();

	public void testShouldRetrieveXsltForGivenScopeAndType()
			throws TransformerException, SAXException, IOException,
			ParserConfigurationException {
		assertConfigEquals("archive-dependencies-metrics-html.xslt", "archive",
				"metrics", "xslt");
	}

	public void testShouldRetrieveShortForGivenScopeAndType()
			throws TransformerException, SAXException, IOException,
			ParserConfigurationException {
		assertConfigEquals("Metrics", "archive", "metrics", "short");
	}

	public void testShouldRetrieveShortForGivenScope()
			throws TransformerException, SAXException, IOException,
			ParserConfigurationException {
		assertConfigEquals("Archive", "archive", "short");
	}

	public void testShouldRetrieveLongForGivenScopeAndType()
			throws TransformerException, SAXException, IOException,
			ParserConfigurationException {
		assertConfigEquals("Archive Metric Report", "archive", "metrics", "long");
	}

	public void testShouldRetrieveLongForGivenScope()
			throws TransformerException, SAXException, IOException,
			ParserConfigurationException {
		assertConfigEquals("Archive Summary Reports", "archive", "long");
	}

	public void assertConfigEquals(String expected, String scope, String type,
			String key) throws FileNotFoundException, TransformerException,
			SAXException, IOException, ParserConfigurationException {
		assertEquals(expected,
				new ConfigParser(new FileInputStream(configPath)).lookupValue(
						scope, type, key));
	}

	public void assertConfigEquals(String expected, String scope, String key)
			throws FileNotFoundException, TransformerException, SAXException,
			IOException, ParserConfigurationException {
		assertEquals(expected,
				new ConfigParser(new FileInputStream(configPath)).lookupValue(
						scope, key));
	}
}
