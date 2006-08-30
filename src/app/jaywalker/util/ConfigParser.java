package jaywalker.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ConfigParser {

	private Document document;

	public ConfigParser(String filename) throws SAXException, IOException,
			ParserConfigurationException {
		this(ConfigParser.class.getResourceAsStream("/META-INF/xml/" + filename));
	}

	public ConfigParser(InputStream inputStream) throws SAXException,
			IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		document = factory.newDocumentBuilder().parse(inputStream);
	}

	public String lookupValue(String scope, String type, String key)
			throws TransformerException {
		String xpath = toXPath(scope, type, key);
		return lookupSingleChildNodeValue(xpath);
	}

	private String lookupSingleChildNodeValue(String xpath)
			throws TransformerException {
		Element element = (Element) XPathAPI.selectSingleNode(document, xpath);
		return element.getChildNodes().item(0).getNodeValue();
	}

	private String toXPath(String scope, String type, String key) {
		String xpath = "/config/content[@scope='" + scope + "'][@type='" + type
				+ "']/" + key;
		return xpath;
	}

}
