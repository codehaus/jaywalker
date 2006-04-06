package jaywalker.report;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Stack;

import jaywalker.classlist.ClassElement;
import jaywalker.classlist.ClasslistContainer;
import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementEvent;
import jaywalker.classlist.ClasslistElementListener;
import jaywalker.classlist.DirectoryContainer;
import jaywalker.xml.TagHelper;

public class AggregateReport implements ClasslistElementListener {
	private final TagHelper reportHelper = new TagHelper();

	private Stack stack = new Stack();

	private final Report[] reports;

	private Writer writer;

	private boolean isEmpty = true;

	private static final String XML_HEADER = "<?xml version=\"1.0\"?>\n<report";

	public AggregateReport(Report[] reports, Writer writer) throws IOException {
		this.reports = reports;
		this.writer = writer;
		writer.write(XML_HEADER);
	}

	public void classlistElementVisited(ClasslistElementEvent event) {
		try {
			final ClasslistElement element = event.getElement();
			final URL url = element.getURL();
			while (isElementNotOnStackTop(element)) {
				stack.pop();
				writer.write(reportHelper.toSpaces(stack.size()));
				writer.write("</container>\n");
			}

			if (isEmpty) {
				writer.write(">\n");
				isEmpty = false;
			}

			writer.write(reportHelper.toSpaces(stack.size()));

			if (element instanceof ClasslistContainer) {
				writer.write("<container type=\"");
				writer.write(element.getType());
				writer.write("\"");
				writer.write(" url=\"");
				writer.write(element.getURL().toString());
				writer.write("\"");
				if (element.getClass() == DirectoryContainer.class) {
					DirectoryContainer directoryContainer = (DirectoryContainer) element;
					final String packageName = directoryContainer
							.getPackageName();
					if (packageName != null) {
						writer.write(" value=\"");
						writer.write(packageName);
						writer.write("\"");
					}
				}
				writer.write(">\n");
				stack.push(element.getURL());
				for (int i = 0; i < reports.length; i++) {
					writer.write(reports[i].toTagString(url, stack));
				}
			} else if (element instanceof ClassElement) {
				ClassElement classElement = (ClassElement) element;
				writer.write("<element type=\"class\" url=\"");
				writer.write(url.toString());
				writer.write("\" value=\"");
				writer.write(classElement.getName());
				writer.write("\"");

				stack.push(url);
				StringBuffer sbXmlTags = new StringBuffer();

				for (int i = 0; i < reports.length; i++) {
					sbXmlTags.append(reports[i].toTagString(url, stack));
				}

				if (sbXmlTags.length() == 0) {
					stack.pop();
					writer.write("/>\n");
					return;
				}

				writer.write(">\n");

				writer.write(sbXmlTags.toString());

				stack.pop();
				writer.write(reportHelper.toSpaces(stack.size()));
				writer.write("</element>\n");

			} else {
				writer.write("<element type=\"");
				writer.write(element.getType());
				writer.write("\"");
				writer.write(" url=\"");
				writer.write(element.getURL().toString());
				writer.write("\"/>\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void lastClasslistElementVisited() {
		try {
			if (isEmpty) {
				writer.write("/>");
				return;
			}

			while (!stack.isEmpty()) {
				writer.write(createContainerEndTag());
			}

			writer.write("</report>");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

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
