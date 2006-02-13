package jaywalker.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XsltTransformer implements Outputter {

	private final ResourceLocator locator = ResourceLocator.instance();

	public final static String DIR_METADATA_JAR = "/META-INF/";

	public final static String DIR_METADATA_SRC = "src" + File.separator
			+ "metadata" + File.separator;

	public final static String DIR_XSLT_JAR = DIR_METADATA_JAR + "xslt/";

	public final static String DIR_XSLT_SRC = DIR_METADATA_SRC + "xslt"
			+ File.separator;

	private final Transformer transformer;

	public XsltTransformer(String filename) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Source xsltSource = new StreamSource(toInputStream(filename));
			Templates templates = factory.newTemplates(xsltSource);
			transformer = templates.newTransformer();
		} catch (Throwable t) {
			throw new OutputterException(
					"Exception thrown while creating XML transformer", t);
		}
	}

	private static InputStream toInputStream(String xsltFile)
			throws FileNotFoundException {
		final File file = new File(DIR_XSLT_SRC + xsltFile);
		if (file.exists()) {
			return new FileInputStream(file);
		} else {
			return XsltTransformer.class.getResourceAsStream(DIR_XSLT_JAR
					+ xsltFile);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jaywalker.util.Outputter#write(java.io.OutputStream)
	 */
	public void write(OutputStream outputStream) {

		try {
			String value = FileSystem.readFileIntoString((File) locator
					.lookup("report.xml"));
			value = transform(value);
			outputStream.write(value.getBytes());
		} catch (FileNotFoundException e) {
			throw new OutputterException(
					"FileNotFoundException thrown while reading in XML file", e);
		} catch (IOException e) {
			throw new OutputterException(
					"IOException thrown while reading in XML file", e);
		}
	}

	public static Outputter[] valueOf(String[] filenames) {
		if (filenames == null || filenames.length == 0) {
			return new Outputter[0];
		}
		Outputter[] transformers = new Outputter[filenames.length];
		for (int i = 0; i < filenames.length; i++) {
			transformers[i] = valueOf(filenames[i]);
		}
		return transformers;
	}

	public static Outputter valueOf(String filename) {
		return new XsltTransformer(filename);
	}

	public static Outputter[] valueOf(String filename1, String filename2) {
		return valueOf(new String[] { filename1, filename2 });
	}

	public String transform(String value) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(value
					.getBytes()));
			Element root = (Element) document.getElementsByTagName("report")
					.item(0);

			DOMSource source = new DOMSource(root);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(baos);
			transformer.transform(source, result);
			return baos.toString();
		} catch (Throwable t) {
			throw new OutputterException(
					"Exception thrown while transforming XML", t);
		}
	}

}