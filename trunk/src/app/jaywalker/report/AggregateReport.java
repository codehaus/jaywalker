package jaywalker.report;

import jaywalker.classlist.*;
import jaywalker.xml.TagHelper;

import java.net.URL;
import java.util.Stack;

public class AggregateReport implements ClasslistElementListener {
	private final TagHelper reportHelper = new TagHelper();

	private String stringValue;

	private StringBuffer sbReport = new StringBuffer();

	private Stack stack = new Stack();

	private final Report[] reports;

	public AggregateReport(Report[] reports) {
		this.reports = reports;
	}

	public void classlistElementVisited(ClasslistElementEvent event) {
		final ClasslistElement element = event.getElement();
		final URL url = element.getURL();
		while (isElementNotOnStackTop(element)) {
			stack.pop();
			sbReport.append(reportHelper.toSpaces(stack.size()));
			sbReport.append("</container>\n");
		}

		sbReport.append(reportHelper.toSpaces(stack.size()));

		if (element instanceof ClasslistContainer) {
			sbReport.append("<container type=\"").append(element.getType())
					.append("\"");
			sbReport.append(" url=\"").append(element.getURL()).append("\"");
			if (element.getClass() == DirectoryContainer.class) {
				DirectoryContainer directoryContainer = (DirectoryContainer) element;
				final String packageName = directoryContainer.getPackageName();
				if (packageName != null) {
					sbReport.append(" value=\"");
					sbReport.append(packageName);
					sbReport.append("\"");
				}
			}
			sbReport.append(">\n");
			stack.push(element.getURL());
			for (int i = 0; i < reports.length; i++) {
				sbReport.append(reports[i].toTagString(url, stack));
			}
		} else if (element instanceof ClassElement) {
			ClassElement classElement = (ClassElement) element;
			sbReport.append("<element type=\"class\" url=\"").append(url)
					.append("\" value=\"");
			sbReport.append(classElement.getName()).append("\"");

			stack.push(url);
			StringBuffer sbXmlTags = new StringBuffer();

			for (int i = 0; i < reports.length; i++) {
				sbXmlTags.append(reports[i].toTagString(url, stack));
			}

			if (sbXmlTags.length() == 0) {
				stack.pop();
				sbReport.append("/>\n");
				return;
			}

			sbReport.append(">\n");

			sbReport.append(sbXmlTags);

			stack.pop();
			sbReport.append(reportHelper.toSpaces(stack.size()));
			sbReport.append("</element>\n");

		} else {
			sbReport.append("<element type=\"").append(element.getType())
					.append("\"");
			sbReport.append(" url=\"").append(element.getURL())
					.append("\"/>\n");
		}

	}

	public void lastClasslistElementVisited() {
		final String header = "<?xml version=\"1.0\"?>\n";
		final StringBuffer sb = new StringBuffer(header);
		sb.append("<report");

		if (sbReport.length() == 0) {
			sb.append("/>");
			stringValue = sb.toString();
		}

		sb.append(">\n");

		sb.append(sbReport);

		while (!stack.isEmpty()) {
			sb.append(createContainerEndTag());
		}

		sb.append("</report>");

		stringValue = sb.toString();

	}

	public String toString() {
		return stringValue;
	}

	private String createContainerEndTag() {
		StringBuffer sb = new StringBuffer();
		stack.pop();
		sb.append(reportHelper.toSpaces(stack.size()));
		sb.append("</container>\n");
		return sb.toString();
	}

	private boolean isElementNotOnStackTop(ClasslistElement element) {
		return !stack.isEmpty()
				&& !element.getContainer().getURL().equals(stack.peek());
	}

}
