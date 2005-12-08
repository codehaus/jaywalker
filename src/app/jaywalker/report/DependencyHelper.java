package jaywalker.report;

import jaywalker.util.URLHelper;

import java.net.URL;
import java.util.*;

public class DependencyHelper {
    private final URLHelper urlHelper = new URLHelper();
    private Map containerDependencyMap;
    private final Map resolvedUrlByClassNameMap = new HashMap();
    private final Map resolvedUrlByPackageNameMap = new HashMap();
    private final Map resolvedClassNameByUrlMap = new HashMap();
    private final Map resolvedPackageNameByUrlMap = new HashMap();
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
        resource = resource + ".class";
        return resource;
    }

    public void resolveSystemClasses() {
        final String [] classNames = (String[]) unresolvedUrlByClassNameMap.keySet().toArray(new String[unresolvedUrlByClassNameMap.size()]);
        for (int i = 0; i < classNames.length; i++) {
            String className = classNames[i];
            URL url = DependencyHelper.class.getResource(asResourceName(className));
            if ( url != null) {
                markAsResolved(url, className);
            }
        }
    }

    private String toPackageName(String className) {
        int idx = className.lastIndexOf('.');
        if (idx != -1) {
            return className.substring(0, idx);
        } else {
            return "";
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
        addToDependencyMap(toPackageName(className), urlHelper.toParentURL(url), resolvedUrlByPackageNameMap);

        if (urlSet != null) {
            for (Iterator it = urlSet.iterator(); it.hasNext();) {
                final URL urlDependency = (URL) it.next();
                addToDependencyMap(urlDependency, className, resolvedClassNameByUrlMap);
                addToDependencyMap(urlHelper.toParentURL(urlDependency), toPackageName(className), resolvedPackageNameByUrlMap);
                removeFromDependencyMap(urlDependency, className, unresolvedClassNameByUrlMap);
            }
        }
    }

    public Map getUnresolvedDependencies() {
        return unresolvedClassNameByUrlMap;
    }

    public Map createContainerDependencyMap() {
        Map map = new HashMap();
        for (Iterator itUrl = resolvedClassNameByUrlMap.keySet().iterator(); itUrl.hasNext();) {
            URL url = (URL) itUrl.next();
            URL containerUrl = urlHelper.toBaseContainerUrl(url);
            final Set classNameSet = (Set) resolvedClassNameByUrlMap.get(url);
            for (Iterator itClassName = classNameSet.iterator(); itClassName.hasNext();) {
                final Set resolvedUrlSet = (Set) resolvedUrlByClassNameMap.get(itClassName.next());
                addResolvedContainers(containerUrl, resolvedUrlSet, map);
            }
        }
        for (Iterator itContainerUrl = map.keySet().iterator(); itContainerUrl.hasNext();) {
            Object key = itContainerUrl.next();
            Set resolvedContainerSet = (Set) map.get(key);
            URL [] resolvedContainers = (URL[]) resolvedContainerSet.toArray(new URL[resolvedContainerSet.size()]);
            map.put(key, resolvedContainers);
        }
        return map;
    }

    public URL [] lookupResolvedContainerDependency(URL url) {
        if (containerDependencyMap == null) {
            containerDependencyMap = createContainerDependencyMap();
        }
        return (URL[]) containerDependencyMap.get(url);
    }

    private void addResolvedContainers(URL containerUrl, Set resolvedUrlSet, Map map) {
        Set resolvedContainerSet = (Set) map.get(containerUrl);
        if (resolvedContainerSet == null) {
            resolvedContainerSet = new HashSet();
            map.put(containerUrl, resolvedContainerSet);
        }
        for (Iterator itResolvedUrl = resolvedUrlSet.iterator(); itResolvedUrl.hasNext();) {
            URL containerDependencyUrl = urlHelper.toBaseContainerUrl((URL) itResolvedUrl.next());
            if (!containerUrl.equals(containerDependencyUrl)) {
                resolvedContainerSet.add(containerDependencyUrl);
            }
        }
    }

    public void updateResolved(URL url, String dependency) {
        addToDependencyMap(url, dependency, resolvedClassNameByUrlMap);
        final String packageName = toPackageName(dependency);
        final URL parentUrl = urlHelper.toParentURL(url);
        addToDependencyMap(parentUrl, packageName, resolvedPackageNameByUrlMap);
        addToDependencyMap(dependency, url, resolvedUrlByClassNameMap);
        addToDependencyMap(packageName, parentUrl, resolvedUrlByPackageNameMap);
    }

    public URL [] lookupResolvedClassDependencies(URL url) {
        return lookupResolvedDependency(url, resolvedClassNameByUrlMap, resolvedUrlByClassNameMap);
    }

    public URL [] lookupResolvedPackageDependencies(URL url) {
        return lookupResolvedDependency(url, resolvedPackageNameByUrlMap, resolvedUrlByPackageNameMap);
    }

    private URL [] lookupResolvedDependency(URL url, Map resolvedNameByUrlMap, Map resolvedUrlByNameMap) {
        Set set = new HashSet();
        Set resolvedNameSet = (Set) resolvedNameByUrlMap.get(url);
        if (resolvedNameSet == null) return new URL[0];
        for (Iterator itNameSet = resolvedNameSet.iterator(); itNameSet.hasNext();) {
            String name = (String) itNameSet.next();
            Set resolvedUrl = (Set) resolvedUrlByNameMap.get(name);
            if (resolvedUrl != null) {
                for (Iterator itUrl = resolvedUrl.iterator(); itUrl.hasNext();) {
                    Object dependencyUrl = itUrl.next();
                    if (! url.equals(dependencyUrl)) {
                        set.add(dependencyUrl);
                        break;
                    }
                }
            }
        }
        return (URL[]) set.toArray(new URL[set.size()]);
    }

    public String [] lookupUnresolvedElementDependency(URL url) {
        final Set set = (Set) unresolvedClassNameByUrlMap.get(url);
        if (set == null) return new String[0];
        return (String[]) set.toArray(new String[set.size()]);
    }

}
