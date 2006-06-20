package jaywalker.report;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import jaywalker.util.ResourceLocator;
import jaywalker.util.StringHelper;
import jaywalker.util.URLHelper;

public class DependencyHelper {

	private final static URLHelper HELPER_URL = new URLHelper();

	private final static StringHelper HELPER_STRING = new StringHelper();

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
		final Set keySet = unresolvedUrlByClassNameMap.keySet();
		final String[] classNames = (String[]) keySet.toArray(new String[keySet
				.size()]);
		ClassLoader cl = createClassLoaderForClasspath();
		for (int i = 0; i < classNames.length; i++) {
			String className = classNames[i];
			String resourceName = asResourceName(className);
			try {
				Class clazz = Class.forName(className, true, cl);
				URL url = clazz.getResource(resourceName);
				markAsResolved(url, className);
			} catch (ClassNotFoundException e) {
				URL url = Object.class.getResource(resourceName);
				markAsResolved(url, className);
			} catch (NoClassDefFoundError e) {
				URL url = Object.class.getResource(resourceName);
				markAsResolved(url, className);
			}
		}
	}

	private ClassLoader createClassLoaderForClasspath() {
		if (!ResourceLocator.instance().contains("classpath")) {
			return Object.class.getClassLoader();
		}
		File[] files = (File[]) ResourceLocator.instance().lookup("classpath");
		URL[] urls = new URL[files.length];
		for (int i = 0; i < files.length; i++) {
			try {
				urls[i] = files[i].toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return new URLClassLoader(urls);
	}

	private boolean isUrlInJayWalkerJarFile(URL url) {
		if (shouldIncludeJayWalkerJarFile()) {
			return false;
		}
		if (url.getProtocol().equals("onejar")) {
			return true;
		}
		String urlString = url.toString();
		int idx = urlString.indexOf("!/");
		if (idx == -1) {
			return false;
		}
		String jarName = new URLHelper()
				.stripProtocolIfTopLevelArchive(urlString.substring(0, idx));
		if (jarName.startsWith("jar:")) {
			jarName = jarName.substring("jar:".length());
		}
		try {
			File file = new File(new URI(jarName));
			return isJayWalkerJarFile(file);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean shouldIncludeJayWalkerJarFile() {
		if (ResourceLocator.instance().contains("includeJayWalkerJarFile")) {
			Boolean value = (Boolean) ResourceLocator.instance().lookup(
					"includeJayWalkerJarFile");
			if (value.booleanValue()) {
				return true;
			}
		}
		return false;
	}

	private boolean isJayWalkerJarFile(File file) {
		try {
			JarFile jarFile = new JarFile(file);
			Manifest manifest = jarFile.getManifest();
			String value = manifest.getMainAttributes().getValue(
					"Implementation-Title");
			if (value == null) {
				return false;
			}
			return value.equals("jaywalker");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private String toPackageName(String className) {
		return HELPER_STRING.substringBeforeLast(className, ".", "");
	}

	private void removeFromDependencyMap(Object key, Object setItem, Map map) {
		Set set = (Set) map.get(key);
		set.remove(setItem);
		if (set.isEmpty())
			map.remove(key);
	}

	public void markAsResolved(URL url, String className) {
		if (url == null
				|| (isUrlInJayWalkerJarFile(url) && !isUrlInJavaLibraries(url))) {
			return;
		}
		Set urlSet = (Set) unresolvedUrlByClassNameMap.remove(className);
		addToDependencyMap(className, url, resolvedUrlByClassNameMap);
		addToDependencyMap(toPackageName(className), HELPER_URL
				.toParentURL(url), resolvedUrlByPackageNameMap);

		if (urlSet != null) {
			for (Iterator it = urlSet.iterator(); it.hasNext();) {
				final URL urlDependency = (URL) it.next();
				addToDependencyMap(urlDependency, className,
						resolvedClassNameByUrlMap);
				addToDependencyMap(HELPER_URL.toParentURL(urlDependency),
						toPackageName(className), resolvedPackageNameByUrlMap);
				removeFromDependencyMap(urlDependency, className,
						unresolvedClassNameByUrlMap);
			}
		}
	}

	private boolean isUrlInJavaLibraries(URL url) {
		return url.toString().indexOf("rt.jar") != -1;
	}

	private Map createContainerDependencyMap() {
		Map map = new HashMap();
		for (Iterator itUrl = resolvedClassNameByUrlMap.keySet().iterator(); itUrl
				.hasNext();) {
			URL url = (URL) itUrl.next();
			URL containerUrl = HELPER_URL.toBaseContainerUrl(url);
			final Set classNameSet = (Set) resolvedClassNameByUrlMap.get(url);
			for (Iterator itClassName = classNameSet.iterator(); itClassName
					.hasNext();) {
				final Set resolvedUrlSet = (Set) resolvedUrlByClassNameMap
						.get(itClassName.next());
				addResolvedContainers(containerUrl, resolvedUrlSet, map);
			}
		}
		for (Iterator itContainerUrl = map.keySet().iterator(); itContainerUrl
				.hasNext();) {
			Object key = itContainerUrl.next();
			Set resolvedContainerSet = (Set) map.get(key);
			URL[] resolvedContainers = (URL[]) resolvedContainerSet
					.toArray(new URL[resolvedContainerSet.size()]);
			map.put(key, resolvedContainers);
		}
		return map;
	}

	public URL[] lookupResolvedContainerDependency(URL url) {
		if (containerDependencyMap == null) {
			containerDependencyMap = createContainerDependencyMap();
		}
		return (URL[]) containerDependencyMap.get(url);
	}

	private void addResolvedContainers(URL containerUrl, Set resolvedUrlSet,
			Map map) {
		Set resolvedContainerSet = (Set) map.get(containerUrl);
		if (resolvedContainerSet == null) {
			resolvedContainerSet = new HashSet();
			map.put(containerUrl, resolvedContainerSet);
		}
		for (Iterator itResolvedUrl = resolvedUrlSet.iterator(); itResolvedUrl
				.hasNext();) {
			URL containerDependencyUrl = HELPER_URL
					.toBaseContainerUrl((URL) itResolvedUrl.next());
			if (!containerUrl.equals(containerDependencyUrl)) {
				resolvedContainerSet.add(containerDependencyUrl);
			}
		}
	}

	public void updateResolved(URL url, String dependency) {
		addToDependencyMap(url, dependency, resolvedClassNameByUrlMap);
		final String packageName = toPackageName(dependency);
		final URL parentUrl = HELPER_URL.toParentURL(url);
		addToDependencyMap(parentUrl, packageName, resolvedPackageNameByUrlMap);
		addToDependencyMap(dependency, url, resolvedUrlByClassNameMap);
		addToDependencyMap(packageName, parentUrl, resolvedUrlByPackageNameMap);
	}

	public URL[] lookupResolvedClassDependencies(URL url) {
		return lookupResolvedDependency(url, resolvedClassNameByUrlMap,
				resolvedUrlByClassNameMap);
	}

	public URL[] lookupResolvedPackageDependencies(URL url) {
		return lookupResolvedDependency(url, resolvedPackageNameByUrlMap,
				resolvedUrlByPackageNameMap);
	}

	private URL[] lookupResolvedDependency(URL url, Map resolvedNameByUrlMap,
			Map resolvedUrlByNameMap) {
		Set set = new HashSet();
		Set resolvedNameSet = (Set) resolvedNameByUrlMap.get(url);
		if (resolvedNameSet == null)
			return new URL[0];
		for (Iterator itNameSet = resolvedNameSet.iterator(); itNameSet
				.hasNext();) {
			String name = (String) itNameSet.next();
			Set resolvedUrl = (Set) resolvedUrlByNameMap.get(name);
			if (resolvedUrl != null) {
				for (Iterator itUrl = resolvedUrl.iterator(); itUrl.hasNext();) {
					Object dependencyUrl = itUrl.next();
					if (!url.equals(dependencyUrl)) {
						set.add(dependencyUrl);
						break;
					}
				}
			}
		}
		return (URL[]) set.toArray(new URL[set.size()]);
	}

	public String[] lookupUnresolvedElementDependency(URL url) {
		return toStrings(unresolvedClassNameByUrlMap, url);
	}

	public String[] lookupResolvedPackageNameDependencies(URL url) {
		return toStrings(resolvedPackageNameByUrlMap, url);
	}

	private String[] toStrings(Map map, URL url) {
		final Set set = (Set) map.get(url);
		if (set == null)
			return new String[0];
		return (String[]) set.toArray(new String[set.size()]);
	}

	public boolean isResolved(URL url) {
		return resolvedClassNameByUrlMap.keySet().contains(url);
	}

}
