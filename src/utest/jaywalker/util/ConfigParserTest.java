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
		assertEquals("archive-dependencies-metrics-html.xslt", "archive",
				"metrics", "xslt");
	}

	public void testShouldRetrieveShortForGivenScopeAndType()
			throws TransformerException, SAXException, IOException,
			ParserConfigurationException {
		assertEquals("Metrics", "archive", "metrics", "short");
	}

	public void testShouldRetrieveLongForGivenScopeAndType()
			throws TransformerException, SAXException, IOException,
			ParserConfigurationException {
		assertEquals("Archive Metric Report", "archive", "metrics", "long");
	}

	public void assertEquals(String expected, String scope, String type,
			String key) throws FileNotFoundException, TransformerException,
			SAXException, IOException, ParserConfigurationException {
		assertEquals(expected,
				new ConfigParser(new FileInputStream(configPath)).lookupValue(
						scope, type, key));
	}
}
