package jaywalker.xml;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import jaywalker.xml.bind.Config;

public abstract class ConfigVisitor {

	protected final OutputStream os;

	protected Config config;

	public ConfigVisitor(OutputStream os) {
		this.os = os;
	}

	public void setConfig(String filename) {
		setConfig(ConfigVisitor.class.getResourceAsStream("/META-INF/xml/"
				+ filename));
	}

	public void setConfig(InputStream is) {
		try {
			JAXBContext jc = JAXBContext.newInstance("jaywalker.xml.bind",
					ConfigVisitor.class.getClassLoader());
			Unmarshaller u = jc.createUnmarshaller();
			setConfig((Config) u.unmarshal(is));
		} catch (JAXBException e) {
			throw new RuntimeException(
					"Exception thrown while processing XML binding for the configuration file.",
					e);
		}
	}

	public void setConfig(Config config) {
		this.config = config;
	}
	
	public abstract void accept();

}
