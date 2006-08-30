package jaywalker.ui;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import jaywalker.util.ConfigParser;

public class ContentMap {

	private Map map = new HashMap();

	private final ConfigParser config;

	public ContentMap() {

		try {
			config = new ConfigParser("jaywalker-config.xml");
			put("archive", "metrics");
			put("archive", "resolved");
			put("archive", "cycle");

			put("package", "metrics");
			put("package", "resolved");
			put("package", "cycle");

			put("class", "collision");
			put("class", "conflict");
			put("class", "unresolved");
			put("class", "cycle");
		} catch (Exception e) {
			throw new RuntimeException(
					"Exception thrown while trying to parse jaywalker-config.xml",
					e);
		}

	}

	public void put(String scope, String type) throws TransformerException {
		put(scope, type, config.lookupValue(scope, type, "xslt").getBytes());
	}

	public void put(String scope, String type, byte[] content) {
		map.put(toKey(scope, type), content);
	}

	public byte[] get(String scope, String type) {
		return (byte[]) map.get(toKey(scope, type));
	}

	private String toKey(String scope, String type) {
		return scope + "-" + type;
	}

}
