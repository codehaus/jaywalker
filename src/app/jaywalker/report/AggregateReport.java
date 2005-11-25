package jaywalker.report;

import jaywalker.classlist.*;
import jaywalker.util.URLHelper;
import jaywalker.util.StringHelper;

import java.net.URL;
import java.util.*;

public class AggregateReport implements ClasslistElementListener {
    private final URLHelper urlHelper = new URLHelper();
    private final DependencyHelper dependencyHelper = new DependencyHelper();
    private final CollisionHelper collisionHelper = new CollisionHelper();
    private final ClasslistElementFactory factory = new ClasslistElementFactory();
    private final StringHelper stringHelper = new StringHelper();
    private final Set containerSet = new TreeSet();
    private final Stack stack = new Stack();

    public void classlistElementVisited(ClasslistElementEvent event) {
        ClasslistElement classlistElement = event.getElement();
        if (classlistElement.getClass() == ClassElement.class) {
            ClassElement classElement = (ClassElement) classlistElement;
            String className = classElement.getName();
            final URL url = classElement.getURL();

            containerSet.add(urlHelper.toBaseContainer(url));

            collisionHelper.register(url, className);

            dependencyHelper.markAsResolved(url, className);
            String [] dependencies = classElement.getDependencies();
            for (int i = 0; i < dependencies.length; i++) {
                String dependency = asClassName(dependencies[i]);
                if (!dependencyHelper.isResolved(dependency)) {
                    dependencyHelper.markAsUnresolved(url, dependency);
                }
            }
        }
    }

    private String asClassName(String dependency) {
        String value = dependency;
        int idx;
        if ((idx = value.lastIndexOf("[")) != -1)
            value = value.substring(idx + 2);
        if (value.endsWith(";"))
            value = value.substring(0, value.length() - 1);
        return value;
    }

    public String toString() {
        final String header = "<?xml version=\"1.0\"?>\n";
        final StringBuffer sb = new StringBuffer(header);
        sb.append("<report");

        dependencyHelper.resolveSystemClasses();

        Map unresolvedMap = dependencyHelper.getUnresolvedDependencies();
        Map urlCollisionMap = collisionHelper.createUrlCollisionMap();
        System.out.println("Collisions: " + urlCollisionMap.size());

        if (unresolvedMap.size() == 0 && urlCollisionMap.size() == 0) {
            sb.append("/>");
            return sb.toString();
        }

        sb.append(">\n");

        Map containerDependencyMap = dependencyHelper.getContainerDependencyMap();
        Map containerMap = createContainerElementUrlsMap(unresolvedMap);
        Map containerElementUrlsMap = createContainerElementUrlsMap(collisionHelper.getUrlToClassNameMap());
        SerialVersionUidHelper suidHelper = new SerialVersionUidHelper(collisionHelper.createClassNameToUrlsMap());

        String [] containers = (String[]) containerSet.toArray(new String[containerSet.size()]);
        Arrays.sort(containers);

        for (int i = 0; i < containers.length; i++) {
            sb.append(createParentContainerTag(containers, i));
            sb.append(createContainerElementTags(containers[i], containerDependencyMap, containerElementUrlsMap, unresolvedMap, urlCollisionMap, suidHelper, containerMap));
        }

        while (!stack.isEmpty()) {
            sb.append(createContainerEndTag());
        }

        sb.append("</report>");

        return sb.toString();

    }

    private String createParentContainerTag(String[] containers, int i) {
        if (i == 0 && containers.length > 1) {
            return createParentContainerTag(containers[0], containers[1]);
        }
        if (i > 0 && i + 1 < containers.length) {
            return createParentContainerTag(containers[i - 1], containers[i], containers[i + 1]);
        }
        if (i > 0 && i < containers.length) {
            return createParentContainerTag(containers[i - 1], containers[i]);
        }
        return "";
    }

    private String createParentContainerTag(String container1, String container2) {
        StringBuffer sb = new StringBuffer();
        String parentContainer = toParentContainerUrlString(container1, container2);
        sb.append(createParentContainerTag(parentContainer));
        return sb.toString();
    }

    private String createParentContainerTag(String parentContainer) {
        StringBuffer sb = new StringBuffer();
        if (isCurrentParentContainer(parentContainer)) {
            while (! stack.isEmpty() && ! stack.peek().equals(parentContainer)) {
                sb.append(createContainerEndTag());
            }
            sb.append(createContainerBeginTag(parentContainer));
        }
        return sb.toString();
    }

    private boolean isCurrentParentContainer(String parentContainer) {
        return stack.isEmpty() || ! stack.peek().equals(parentContainer);
    }

    private String createContainerEndTag() {
        StringBuffer sb = new StringBuffer();
        stack.pop();
        sb.append(toSpaces(stack.size()));
        sb.append("</container>\n");
        return sb.toString();
    }

    private String createParentContainerTag(String container1, String container2, String container3) {
        StringBuffer sb = new StringBuffer();
        String parentContainer1 = toParentContainerUrlString(container1, container2);
        String parentContainer2 = toParentContainerUrlString(container2, container3);
        if (!parentContainer1.equals(parentContainer2)) {
            sb.append(createContainerBeginTag(parentContainer2));
        } else {
            sb.append(createParentContainerTag(parentContainer1));
        }
        return sb.toString();
    }

    private String createContainerBeginTag(String container) {
        StringBuffer sb = new StringBuffer();
        sb.append(toSpaces(stack.size()));
        sb.append("<container type=\"archive\" url=\"").append(container).append("\">\n");
        stack.push(container);
        return sb.toString();
    }

    private String toParentContainerUrlString(String container1, String container2) {
        String commonPrefix = stringHelper.extractCommonPrefix(container1, container2);
        String parentContainer = toParentContainerUrlString(commonPrefix);
        return parentContainer;
    }

    private String toParentContainerUrlString(String containerUrlString) {
        String parentContainer = containerUrlString.substring(0, containerUrlString.lastIndexOf('/'));
        if (parentContainer.endsWith("!")) return parentContainer.substring(0, parentContainer.length() - 1);
        return parentContainer;
    }

    private String createContainerElementTags(String urlString, Map containerDependencyMap, Map containerElementUrlsMap, Map unresolvedMap, Map urlCollisionMap, SerialVersionUidHelper suidHelper, Map containerMap) {
        StringBuffer sb = new StringBuffer();

        Set containerDependencySet = (Set) containerDependencyMap.get(urlString);
        URL [] containerElementUrls = (URL[]) containerElementUrlsMap.get(urlString);

        StringBuffer sbContainer = new StringBuffer();

        sbContainer.append(toSpaces(stack.size()));
        sbContainer.append("<container type=\"archive\" url=\"").append(urlString).append("\">\n");
        int sbContainerOriginalLength = sbContainer.length();

        stack.push(urlString);
        sbContainer.append(createResolvedContainerDependenciesTag(containerDependencySet));

        if (containerElementUrls != null) {
            for (int j = 0; j < containerElementUrls.length; j++) {
                URL url = containerElementUrls[j];
                Set unresolvedUrlSet = (Set) unresolvedMap.get(url);
                URL [] collisionUrls = (URL[]) urlCollisionMap.get(url);

                if (collisionUrls == null && unresolvedUrlSet == null) continue;

                sbContainer.append(toSpaces(stack.size()));
                ClassElement classElement = (ClassElement) factory.create(url);
                sbContainer.append("<element type=\"class\" url=\"").append(url).append("\" value=\"");
                sbContainer.append(classElement.getName()).append("\">\n");

                stack.push(url.toString());

                if (collisionUrls != null) {

                    boolean isConflict = suidHelper.isSerialVersionUidsConflicting(collisionUrls);

                    for (int k = 0; k < collisionUrls.length; k++) {
                        sbContainer.append(toSpaces(stack.size()));
                        sbContainer.append("<collision url=\"").append(collisionUrls[k]).append("\"");
                        if (!isConflict) {
                            sbContainer.append("/>\n");
                        } else {
                            sbContainer.append(">\n");
                            sbContainer.append(createSerialVersionUidConflictTag(suidHelper.toSerialVersionUID(collisionUrls[k])));
                            sbContainer.append(toSpaces(stack.size()));
                            sbContainer.append("</collision>\n");
                        }
                    }
                }

                if (unresolvedUrlSet != null) {
                    sbContainer.append(createUnresolvedDependencyTags(unresolvedUrlSet));
                    unresolvedMap.remove(url);
                }

                stack.pop();

                sbContainer.append(toSpaces(stack.size()));
                sbContainer.append("</element>\n");
            }

        }

        stack.pop();

        URL [] unresolves = (URL[]) containerMap.get(urlString);
        sbContainer.append(createElementsWithUnresolvedDependencyTags(unresolves, unresolvedMap));

        if (sbContainerOriginalLength != sbContainer.length()) {
            sbContainer.append(toSpaces(stack.size()));
            sbContainer.append("</container>\n");
            sb.append(sbContainer);
        }

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

    private String createElementsWithUnresolvedDependencyTags(URL[] unresolves, Map unresolvedMap) {
        if (unresolves == null) return "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < unresolves.length; i++) {
            Set set = (Set) unresolvedMap.get(unresolves[i]);
            if (set != null) {
                sb.append(toSpaces(stack.size()));
                sb.append("<element type=\"class\" url=\"").append(unresolves[i]).append("\" value=\"");
                sb.append(((ClassElement) factory.create(unresolves[i])).getName());
                sb.append("\">\n");
                stack.push(unresolves[i]);
                sb.append(createUnresolvedDependencyTags(set));
                stack.pop();
                sb.append(toSpaces(stack.size()));
                sb.append("</element>\n");
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

    private Map createContainerElementUrlsMap(Map map) {
        Map containerMap = new HashMap();
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
            URL url = (URL) it.next();

            String containerName = urlHelper.toBaseContainer(url);
            Set set = (Set) containerMap.get(containerName);
            if (set == null) {
                set = new HashSet();
                containerMap.put(containerName, set);
            }
            set.add(url);

        }
        convertUrlValuesFromSetToArray(containerMap);
        return containerMap;
    }

    private void convertUrlValuesFromSetToArray(Map map) {
        for (Iterator itKey = map.keySet().iterator(); itKey.hasNext();) {
            final Object key = itKey.next();
            Set set = (Set) map.get(key);
            map.put(key, set.toArray(new URL[set.size()]));
        }
    }

    private String toSpaces(int spaces) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= spaces; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

}
