package jaywalker.report;

import jaywalker.classlist.*;

import java.net.URL;
import java.util.Stack;

public class AggregateReport implements ClasslistElementListener {
    private String stringValue;

    StringBuffer sbReport = new StringBuffer();
    Stack stack = new Stack();
    private final Report[] reports;

    public AggregateReport(Report [] reports) {
        this.reports = reports;
    }

    public void classlistElementVisited(ClasslistElementEvent event) {
        final ClasslistElement element = event.getElement();
        final URL url = element.getURL();
        while (isElementNotOnStackTop(element)) {
            stack.pop();
            sbReport.append(toSpaces(stack.size()));
            sbReport.append("</container>\n");
        }

        sbReport.append(toSpaces(stack.size()));

        if (element instanceof ClasslistContainer) {
            sbReport.append("<container type=\"").append(element.getType()).append("\"");
            sbReport.append(" url=\"").append(element.getURL()).append("\">\n");
            stack.push(element.getURL());
            for (int i = 0; i < reports.length; i++) {
                sbReport.append(reports[i].createContainerSection(url, stack));
            }
        } else if (element instanceof ClassElement) {
            ClassElement classElement = (ClassElement) element;
            sbReport.append("<element type=\"class\" url=\"").append(url).append("\" value=\"");
            sbReport.append(classElement.getName()).append("\"");

            stack.push(url);
            String [] xmlTags = new String [reports.length];

            for (int i = 0; i < reports.length; i++) {
                xmlTags[i] = reports[i].createElementSection(url, stack);
            }

            if (isAllBlank(xmlTags)) {
                stack.pop();
                sbReport.append("/>\n");
                return;
            }

            sbReport.append(">\n");

            for (int i = 0; i < xmlTags.length; i++) {
                sbReport.append(xmlTags[i]);
            }

            stack.pop();
            sbReport.append(toSpaces(stack.size()));
            sbReport.append("</element>\n");

        } else {
            sbReport.append("<element type=\"").append(element.getType()).append("\"");
            sbReport.append(" url=\"").append(element.getURL()).append("\"/>\n");
        }

    }

    private boolean isAllBlank(String[] xmlTags) {
        for (int i = 0; i < xmlTags.length; i++) {
            if (xmlTags[i].length() > 0) return false;
        }
        return true;
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
        sb.append(toSpaces(stack.size()));
        sb.append("</container>\n");
        return sb.toString();
    }

    private boolean isElementNotOnStackTop(ClasslistElement element) {
        return !stack.isEmpty() && !element.getContainer().getURL().equals(stack.peek());
    }

    private String toSpaces(int spaces) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= spaces; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

}
