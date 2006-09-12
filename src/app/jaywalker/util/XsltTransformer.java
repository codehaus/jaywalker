package jaywalker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


public class XsltTransformer implements Outputter {

	private final ResourceLocator locator = ResourceLocator.instance();

	public final static String DIR_METADATA_JAR = "/META-INF/";

	public final static String DIR_METADATA_SRC = "src" + File.separator
			+ "metadata" + File.separator;

	public final static String DIR_XSLT_JAR = DIR_METADATA_JAR + "xslt/";

	public final static String DIR_XSLT_SRC = DIR_METADATA_SRC + "xslt"
			+ File.separator;

	private final Transformer transformer;

	private class CustomURIResolver implements URIResolver {

		public Source resolve(String href, String base)
				throws TransformerException {
			try {
				return new StreamSource(toInputStream(href));
			} catch (FileNotFoundException e) {
				throw new TransformerException(
						"FileNotFoundException thrown while creating Source for href: "
								+ href, e);
			}
		}
	}

	public XsltTransformer(String filename) {
		try {
			TransformerFactory factory = lookupTransformerFactory();
			Source xsltSource = new StreamSource(toInputStream(filename));
			transformer = factory.newTransformer(xsltSource);
		} catch (Throwable t) {
			throw new OutputterException(
					"Exception thrown while creating XML transformer: "
							+ filename, t);
		}
	}

	private TransformerFactory lookupTransformerFactory()
			throws TransformerFactoryConfigurationError {
		if (!locator.contains("TransformerFactory")) {
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setURIResolver(new CustomURIResolver());
			locator.register("TransformerFactory", factory);
		}
		return (TransformerFactory) locator.lookup("TransformerFactory");
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

	public void write(OutputStream outputStream) {
		FileDecorator reportFile = (FileDecorator) locator.lookup("report.xml");
		Reader reader = reportFile.getReader();
		InputStream inputStream = new ReaderInputStream(reader);
		transform(inputStream, outputStream);
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

	public void transform(InputStream is, OutputStream os) {
		try {
			StreamSource source = new StreamSource(is);
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
		} catch (Throwable t) {
			throw new OutputterException(
					"Exception thrown while transforming XML", t);
		}
	}

}
