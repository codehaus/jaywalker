package jaywalker.report;

import java.net.URL;
import java.util.Stack;


public class Report {

	private final Tag[] tags;

	public Report(Tag[] tags) {
		this.tags = tags;
	}

	public String toTagString(URL url, Stack parentUrlStack) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tags.length; i++) {
			sb.append(tags[i].create(url, parentUrlStack));
		}
		return sb.toString();
	}

}
