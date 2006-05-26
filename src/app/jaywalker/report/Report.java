package jaywalker.report;

import java.io.OutputStream;
import java.net.URL;
import java.util.Stack;

import jaywalker.util.Outputter;

public class Report {

	private final Tag[] tags;

	private final Outputter[] transformers;

	public Report(Tag[] tags, Outputter[] transformers) {
		this.tags = tags;
		this.transformers = transformers;
	}

	public String toTagString(URL url, Stack parentUrlStack) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tags.length; i++) {
			sb.append(tags[i].create(url, parentUrlStack));
		}
		return sb.toString();
	}

	public void transform(OutputStream os) {
		for (int i = 0; i < transformers.length; i++) {
			if (transformers[i] != null) {
				transformers[i].write(os);
			}
		}
	}

}
