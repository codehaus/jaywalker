package jaywalker.ui;

import java.util.HashMap;
import java.util.Map;

public class ContentMap {

	private Map map = new HashMap();

	public ContentMap() {
		put("archive", "metrics", "archive-dependencies-metrics-html.xslt"
				.getBytes());
		put("archive", "resolved", "archive-dependencies-resolve-html.xslt"
				.getBytes());
		put("archive", "cycle", "archive-dependencies-cycle-html.xslt"
				.getBytes());

		put("package", "metrics", "package-dependencies-metrics-html.xslt"
				.getBytes());
		put("package", "resolved", "package-dependencies-resolved-html.xslt"
				.getBytes());
		put("package", "cycle", "package-dependencies-cycle-html.xslt"
				.getBytes());

		put("class", "collision", "class-collisions-resolved-html.xslt"
				.getBytes());
		put("class", "conflict", "class-conflicts-resolved-html.xslt"
				.getBytes());
		put("class", "unresolved", "class-dependencies-unresolved-html.xslt"
				.getBytes());
		put("class", "cycle", "class-dependencies-cycle-html.xslt".getBytes());
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
