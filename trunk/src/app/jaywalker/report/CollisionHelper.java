package jaywalker.report;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jaywalker.classlist.ClassElement;

public class CollisionHelper {

	private final Map classNameToUrlsMap = new TreeMap();

	public void terminate() {
		classNameToUrlsMap.clear();
	}
	
	public void register(ClassElement classElement) {
		URL url = classElement.getURL();
		String className = classElement.getName();
		lookupUrlCollisionList(className).add(url);
	}

	private List lookupUrlCollisionList(String className) {
		List collisionList = (List) classNameToUrlsMap.get(className);
		if (collisionList == null) {
			collisionList = new ArrayList();
			classNameToUrlsMap.put(className, collisionList);
		}
		return collisionList;
	}

	public Map createUrlCollisionMap() {
		final Map map = new HashMap();
		final Set keySet = classNameToUrlsMap.keySet();
		final String[] keys = (String[]) keySet.toArray(new String[keySet
				.size()]);
		for (int i = 0; i < keys.length; i++) {
			List collisionList = (List) classNameToUrlsMap.get(keys[i]);
			URL[] urls = (URL[]) collisionList.toArray(new URL[collisionList
					.size()]);
			updateUrlCollisionMap(map, urls);
		}
		return map;
	}

	private void updateUrlCollisionMap(final Map map, URL[] urls) {
		for (int i = 0; i < urls.length; i++) {
			for (int j = 0, k = 0; j < urls.length; j++) {
				if (i != j) {
					URL[] collisions = (URL[]) map.get(urls[i]);
					if (collisions == null) {
						collisions = new URL[urls.length - 1];
						map.put(urls[i], collisions);
					}
					collisions[k++] = urls[j];
				}
			}
		}
	}

	public Map createClassNameToUrlMap() {
		final Map map = new HashMap();
		final Set keySet = classNameToUrlsMap.keySet();
		final String[] keys = (String[]) keySet.toArray(new String[keySet
				.size()]);
		for (int i = 0; i < keys.length; i++) {
			Collection collisionList = (Collection) classNameToUrlsMap
					.get(keys[i]);
			URL[] urls = (URL[]) collisionList.toArray(new URL[collisionList
					.size()]);
			map.put(keys[i], urls);
		}
		return map;
	}

}
