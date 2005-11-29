package jaywalker.report;

import jaywalker.classlist.*;

import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class AggregateReport implements ClasslistElementListener {
    private final ClasslistElementFactory factory = new ClasslistElementFactory();
    private String stringValue;
    private final AggregateModel model;

    StringBuffer sb = new StringBuffer();
    Stack stack = new Stack();

    public AggregateReport(AggregateModel model) {
        this.model = model;
    }

    public void classlistElementVisited(ClasslistElementEvent event) {
        final ClasslistElement element = event.getElement();
        final URL url = element.getURL();
        if (isParentOnStackTop(element)) {
            while (isParentOnStackTop(element)) {
                stack.pop();
                sb.append(toSpaces(stack.size()));
                sb.append("</container>\n");
            }
        }

        sb.append(toSpaces(stack.size()));

        if (element instanceof ClasslistContainer) {
            sb.append("<container type=\"").append(element.getType()).append("\"");
            sb.append(" url=\"").append(element.getURL()).append("\">\n");
            // container dependencies
            String urlString = element.getURL().toString();
            Set containerDependencySet = model.lookupContainerDependencies(urlString);
            stack.push(element.getURL());
            sb.append(createResolvedContainerDependenciesTag(containerDependencySet));
        } else if (element instanceof ClassElement) {
            ClassElement classElement = (ClassElement) factory.create(url);
            sb.append("<element type=\"class\" url=\"").append(url).append("\" value=\"");
            sb.append(classElement.getName()).append("\"");

            URL [] collisionUrls = model.lookupCollisionUrls(url);
            Set unresolvedUrlSet = model.lookupUnresolvedElementDependencies(url);

            if (collisionUrls == null && unresolvedUrlSet == null) {
                sb.append("/>\n");
                return;
            }

            sb.append(">\n");

            stack.push(url.toString());

            if (collisionUrls != null) {

                boolean isConflict = model.isSerialVersionUidsConflicting(collisionUrls);

                for (int k = 0; k < collisionUrls.length; k++) {
                    sb.append(toSpaces(stack.size()));
                    sb.append("<collision url=\"").append(collisionUrls[k]).append("\"");
                    if (!isConflict) {
                        sb.append("/>\n");
                    } else {
                        sb.append(">\n");
                        sb.append(createSerialVersionUidConflictTag(model.toSerialVersionUID(collisionUrls[k])));
                        sb.append(toSpaces(stack.size()));
                        sb.append("</collision>\n");
                    }
                }
            }

            if (unresolvedUrlSet != null) {
                sb.append(createUnresolvedDependencyTags(unresolvedUrlSet));
            }

        } else {
            sb.append("<element type=\"").append(element.getType()).append("\"");
            sb.append(" url=\"").append(element.getURL()).append("\"/>\n");
        }

    }

    public void lastClasslistElementVisited() {
        final String header = "<?xml version=\"1.0\"?>\n";
        final StringBuffer sb = new StringBuffer(header);
        sb.append("<report");

        if (this.sb.length() == 0) {
            sb.append("/>");
            stringValue = sb.toString();
        }

        sb.append(">\n");

        sb.append(this.sb);

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

    private String createSerialVersionUidConflictTag(long serialVersionUid) {
        StringBuffer sb = new StringBuffer();
        sb.append(toSpaces(stack.size() + 1));
        sb.append("<conflict type=\"serialVersionUid\" value=\"");
        sb.append(serialVersionUid);
        sb.append("\"/>\n");
        return sb.toString();
    }

    private String createResolvedContainerDependenciesTag(Set containerDependencySet) {
        StringBuffer sb = new StringBuffer();
        if (containerDependencySet != null) {
            for (Iterator itContainerDependency = containerDependencySet.iterator(); itContainerDependency.hasNext();) {
                sb.append(toSpaces(stack.size()));
                sb.append("<dependency type=\"resolved\" value=\"").append(itContainerDependency.next());
                sb.append("\"/>\n");
            }
        }
        return sb.toString();
    }

    private String createUnresolvedDependencyTags(Set unresolvedSet) {
        StringBuffer sb = new StringBuffer();
        for (Iterator it2 = unresolvedSet.iterator(); it2.hasNext();) {
            sb.append(toSpaces(stack.size()));
            sb.append("<dependency type=\"unresolved\" value=\"");
            sb.append(it2.next());
            sb.append("\"/>\n");
        }
        return sb.toString();
    }

    private boolean isParentOnStackTop(ClasslistElement element) {
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
