package jaywalker.report;

import jaywalker.util.URLHelper;

import java.net.URL;
import java.util.*;

public class DependencyHelper {
    private final URLHelper urlHelper = new URLHelper();
    private final Map resolvedUrlByClassNameMap = new HashMap();
    private final Map resolvedClassNameByUrlMap = new HashMap();
    private final Map unresolvedClassNameByUrlMap = new HashMap();
    private final Map unresolvedUrlByClassNameMap = new HashMap();

    public boolean isResolved(String className) {
        return resolvedUrlByClassNameMap.keySet().contains(className);
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
        resolvedUrlByClassNameMap.put(className, null);
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
        Set urlSet = (Set) unresolvedUrlByClassNameMap.remove(className);
        addToDependencyMap(className, url, resolvedUrlByClassNameMap);

        if (urlSet != null) {
            for (Iterator it = urlSet.iterator(); it.hasNext();) {
                final URL urlDependency = (URL) it.next();
                addToDependencyMap(url, className, resolvedClassNameByUrlMap);
                removeFromDependencyMap(urlDependency, className, unresolvedClassNameByUrlMap);
            }
        }
    }

    public Map getUnresolvedDependencies() {
        return unresolvedClassNameByUrlMap;
    }

    public Map getContainerDependencyMap() {
        Map map = new HashMap();
        for (Iterator itUrl = resolvedClassNameByUrlMap.keySet().iterator(); itUrl.hasNext();) {
            URL url = (URL) itUrl.next();
            String containerUrlString = urlHelper.toBaseContainer(url);
            final Set classNameSet = (Set) resolvedClassNameByUrlMap.get(url);
            for (Iterator itClassName = classNameSet.iterator(); itClassName.hasNext();) {
                final Set resolvedUrlSet = (Set) resolvedUrlByClassNameMap.get(itClassName.next());
                addResolvedContainers(containerUrlString, resolvedUrlSet, map);
            }
        }
        return map;
    }

    private void addResolvedContainers(String containerUrlString, Set resolvedUrlSet, Map map) {
        Set resolvedContainerSet = (Set) map.get(containerUrlString);
        if (resolvedContainerSet == null) {
            resolvedContainerSet = new HashSet();
            map.put(containerUrlString, resolvedContainerSet);
        }
        for (Iterator itResolvedUrl = resolvedUrlSet.iterator(); itResolvedUrl.hasNext();) {
            resolvedContainerSet.add(urlHelper.toBaseContainer((URL) itResolvedUrl.next()));
        }
    }

    public void updateResolved(URL url, String dependency) {
        addToDependencyMap(url, dependency, resolvedClassNameByUrlMap);
        addToDependencyMap(dependency, url, resolvedUrlByClassNameMap);
    }
}
