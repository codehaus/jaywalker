package jaywalker.report;

import java.net.URL;
import java.util.Stack;

import jaywalker.classlist.ClassElement;
import jaywalker.classlist.ClasslistElement;

public class ClassElementTag implements ClasslistElementTag {
	private final static TagHelper HELPER_TAG = new TagHelper();

	public String create(ClasslistElement element, Report[] reports,
			Stack parentUrlStack) {
		final ClassElement classElement = (ClassElement) element;
		final StringBuffer sb = new StringBuffer();
		final URL url = classElement.getURL();
		sb.append("<element type=\"class\" url=\"");
		sb.append(url.toString());
		sb.append("\" value=\"");
		sb.append(classElement.getName());
		sb.append("\"");

		String tags = toTags(url, parentUrlStack, reports);

		if (tags.length() == 0) {
			sb.append("/>\n");
		} else {
			sb.append(">\n");
			sb.append(tags.toString());
			sb.append(HELPER_TAG.toSpaces(parentUrlStack.size()));
			sb.append("</element>\n");
		}

		return sb.toString();
	}

	private String toTags(final URL url, Stack parentUrlStack, Report[] reports) {
		StringBuffer sb = new StringBuffer();

		parentUrlStack.push(url);

		for (int i = 0; i < reports.length; i++) {
			sb.append(reports[i].toTagString(url, parentUrlStack));
		}

		parentUrlStack.pop();

		return sb.toString();
	}

}
