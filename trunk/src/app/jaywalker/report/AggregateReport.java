package jaywalker.report;

import jaywalker.classlist.*;
import jaywalker.util.URLHelper;

import java.net.URL;
import java.util.*;

public class AggregateReport implements ClasslistElementListener {
    private final URLHelper urlHelper = new URLHelper();
    private final DependencyHelper dependencyHelper = new DependencyHelper();
    private final CollisionHelper collisionHelper = new CollisionHelper();
    private final ClasslistElementFactory factory = new ClasslistElementFactory();
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

        for (int i = 0; i < containers.length; i++) {
            String urlString = containers[i];

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

                    if (collisionUrls != null) {

                        stack.push(url.toString());
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
                        stack.pop();
                    }

                    if (unresolvedUrlSet != null) {
                        sbContainer.append(createUnresolvedDependencyTags(unresolvedUrlSet));
                        unresolvedMap.remove(url);
                    }

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
        }

        sb.append("</report>");

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
                sb.append(createUnresolvedDependencyTags(set));
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
