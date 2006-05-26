package jaywalker.report;

import java.util.Stack;

import jaywalker.classlist.ClasslistElement;

public class DefaultClasslistElementTag implements ClasslistElementTag {

	public String create(ClasslistElement element, Report[] reports, Stack parentUrlStack) {
		final StringBuffer sb = new StringBuffer();
		sb.append("<element type=\"");
		sb.append(element.getType());
		sb.append("\"");
		sb.append(" url=\"");
		sb.append(element.getURL().toString());
		sb.append("\"/>\n");
		return sb.toString();
	}

}
