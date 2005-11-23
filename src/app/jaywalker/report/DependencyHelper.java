package jaywalker.report;

import jaywalker.util.URLHelper;

import java.net.URL;
import java.util.*;

public class DependencyHelper {
    private final URLHelper urlHelper = new URLHelper();
    private final Set resolvedClassName = new HashSet();
    private final Map containerDependencyMap = new HashMap();
    private final Map unresolvedClassNameByUrlMap = new HashMap();
    private final Map unresolvedUrlByClassNameMap = new HashMap();

    public boolean isResolved(String className) {
        return resolvedClassName.contains(className);
    }

    public void markAsUnresolved(URL url, String className) {
        addToDependencyMap(url, className, unresolvedClassNameByUrlMap);
        addToDependencyMap(className, url, unresolvedUrlByClassNameMap);
    }

    private void addToDependencyMap(Object key, Object setItem, Map map) {
        Set set = (Set) map.get(key);
        if (set == null) {
            set = new HashSet();
            map.put(key, set);
        }
        set.add(setItem);
    }

    private String asResourceName(String resource) {
        if (!resource.startsWith("/")) {
            resource = "/" + resource;
        }
        resource = resource.replace('.', '/');
        //resource = resource.replace('$', '.');
        resource = resource + ".class";
        return resource;
    }

    public void resolveSystemClasses() {
        final String [] classNames = (String[]) unresolvedUrlByClassNameMap.keySet().toArray(new String[unresolvedUrlByClassNameMap.size()]);
        for (int i = 0; i < classNames.length; i++) {
            String className = classNames[i];
            if (DependencyHelper.class.getResource(asResourceName(className)) != null) {
                markAsResolved(className);
            }
        }
    }

    public void markAsResolved(String className) {
        resolvedClassName.add(className);
        Set urlSet = (Set) unresolvedUrlByClassNameMap.remove(className);

        if (urlSet != null) {
            for (Iterator it = urlSet.iterator(); it.hasNext();) {
                final URL urlDependency = (URL) it.next();
                removeFromDependencyMap(urlDependency, className, unresolvedClassNameByUrlMap);
            }
        }

    }

    private void removeFromDependencyMap(Object key, Object setItem, Map map) {
        Set set = (Set) map.get(key);
        set.remove(setItem);
        if (set.isEmpty()) map.remove(key);
    }

    public void markAsResolved(URL url, String className) {

        String containerName = urlHelper.toBaseContainer(url);
        resolvedClassName.add(className);
        Set urlSet = (Set) unresolvedUrlByClassNameMap.remove(className);

        if (urlSet != null) {
            for (Iterator it = urlSet.iterator(); it.hasNext();) {
                final URL urlDependency = (URL) it.next();
                String containerDependencyName = urlHelper.toBaseContainer(urlDependency);
                if (containerName != null && !containerName.equals(containerDependencyName)) {
                    addContainerDependency(containerName, containerDependencyName);
                }
                removeFromDependencyMap(urlDependency, className, unresolvedClassNameByUrlMap);
            }
        }
    }

    private void addContainerDependency(String containerName, String containerDependencyName) {
        Set containerNameSet = (Set) containerDependencyMap.get(containerName);
        if (containerNameSet == null) {
            containerNameSet = new HashSet();
            containerDependencyMap.put(containerName, containerNameSet);
        }
        containerNameSet.add(containerDependencyName);
    }

    public Map getUnresolvedDependencies() {
        return unresolvedClassNameByUrlMap;
    }

    public Map getContainerDependencyMap() {
        return containerDependencyMap;
    }

}
