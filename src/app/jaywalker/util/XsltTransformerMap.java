package jaywalker.util;

import java.util.HashMap;
import java.util.Map;

public class XsltTransformerMap {

	private Map map = new HashMap();

	public XsltTransformerMap() {
		put("archive", "metrics", "archive-dependencies-metrics-html.xslt");
		put("archive", "resolved", "archive-dependencies-resolved-html.xslt");
		put("archive", "cycle", "archive-dependencies-cycle-html.xslt");

		put("package", "metrics", "package-dependencies-metrics-html.xslt");
		put("package", "resolved", "package-dependencies-resolved-html.xslt");
		put("package", "cycle", "package-dependencies-cycle-html.xslt");

		put("class", "collision", "class-collisions-resolved-html.xslt");
		put("class", "conflict", "class-conflicts-resolved-html.xslt");
		put("class", "unresolved", "class-dependencies-unresolved-html.xslt");
		put("class", "cycle", "class-dependencies-cycle-html.xslt");
	}

	public void put(String scope, String type, String filename) {
		map.put(toKey(scope, type), new XsltTransformer(filename));
	}

	public XsltTransformer get(String scope, String type) {
		return (XsltTransformer) map.get(toKey(scope, type));
	}

	private String toKey(String scope, String type) {
		return scope + "-" + type;
	}

}
