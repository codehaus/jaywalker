package jaywalker.report;

import java.net.URL;
import java.util.Stack;

import jaywalker.classlist.ClasslistElement;

public class DefaultClasslistContainerTag implements ClasslistElementTag {

	public String create(ClasslistElement element, Report[] reports,
			Stack parentUrlStack) {
		final StringBuffer sb = new StringBuffer();
		final URL url = element.getURL();
		sb.append("<container type=\"");
		sb.append(element.getType());
		sb.append("\"");
		sb.append(" url=\"");
		sb.append(url.toString());
		sb.append("\"");
		sb.append(toAttributeString(element));
		sb.append(">\n");
		parentUrlStack.push(url);
		for (int i = 0; i < reports.length; i++) {
			sb.append(reports[i].toTagString(url, parentUrlStack));
		}
		return sb.toString();
	}

	protected String toAttributeString(final ClasslistElement element) {
		return "";
	}

}
