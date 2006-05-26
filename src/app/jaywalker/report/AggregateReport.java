package jaywalker.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Stack;

import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementEvent;
import jaywalker.classlist.ClasslistElementListener;

public class AggregateReport implements ClasslistElementListener {

	private final static TagHelper HELPER_TAG = new TagHelper();

	private Stack stack = new Stack();

	private final Report[] reports;

	private Writer writer;

	private boolean isEmpty = true;

	private final static String XML_HEADER = "<?xml version=\"1.0\"?>\n<report";

	private final static ClasslistElementTagMediator TAG_MEDIATOR = new ClasslistElementTagMediator();

	public AggregateReport(Report[] reports, Writer writer) throws IOException {
		this.reports = reports;
		this.writer = writer;
		writer.write(XML_HEADER);
	}

	public void classlistElementVisited(ClasslistElementEvent event) {
		try {
			final StringBuffer sb = new StringBuffer();
			final ClasslistElement element = event.getElement();

			while (isElementNotOnStackTop(element)) {
				sb.append(createContainerEndTag());
			}

			if (isEmpty) {
				sb.append(">\n");
				isEmpty = false;
			}

			sb.append(HELPER_TAG.toSpaces(stack.size()));

			sb.append(TAG_MEDIATOR.create(element, reports, stack));

			writer.write(sb.toString());
			writer.flush();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void lastClasslistElementVisited() {
		try {
			if (isEmpty) {
				writer.write("/>");
				writer.flush();
				return;
			}

			StringBuffer sb = new StringBuffer();
			while (!stack.isEmpty()) {
				sb.append(createContainerEndTag());
			}

			sb.append("</report>");

			writer.write(sb.toString());
			writer.flush();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private String createContainerEndTag() {
		StringBuffer sb = new StringBuffer();
		stack.pop();
		sb.append(HELPER_TAG.toSpaces(stack.size()));
		sb.append("</container>\n");
		return sb.toString();
	}

	private boolean isElementNotOnStackTop(ClasslistElement element) {
		return !stack.isEmpty()
				&& !element.getContainer().getURL().equals(stack.peek());
	}

	public void transform(OutputStream fos) {
		for (int i = 0; i < reports.length; i++) {
			reports[i].transform(fos);
		}
	}

}
