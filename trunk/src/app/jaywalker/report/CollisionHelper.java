package jaywalker.report;

import java.net.URL;
import java.util.*;

public class CollisionHelper {

    private Map classNameToUrlsMap = new TreeMap();
    private Map urlToClassNameMap = new HashMap();

    public void register(URL url, String className) {
        List collisionList = (List) classNameToUrlsMap.get(className);
        if (collisionList == null) {
            collisionList = new ArrayList();
            classNameToUrlsMap.put(className, collisionList);
        }
        urlToClassNameMap.put(url, className);
        collisionList.add(url);
    }

    public Map createUrlCollisionMap() {
        final Map map = new HashMap();
        for (Iterator itKeys = classNameToUrlsMap.keySet().iterator(); itKeys.hasNext();) {
            String key = (String) itKeys.next();
            List collisionList = (List) classNameToUrlsMap.get(key);
            URL [] urls = (URL[]) collisionList.toArray(new URL[collisionList.size()]);
            for (int i = 0; i < urls.length; i++) {
                for (int j = 0, k = 0; j < urls.length; j++) {
                    if (i != j) {
                        URL [] collisions = (URL[]) map.get(urls[i]);
                        if (collisions == null) {
                            collisions = new URL[urls.length - 1];
                            map.put(urls[i], collisions);
                        }
                        collisions[k++] = urls[j];
                    }
                }
            }
        }
        return map;
    }

    public Map createClassNameToUrlsMap() {
        Map map = new HashMap();
        for ( Iterator it = classNameToUrlsMap.keySet().iterator();it.hasNext();) {
            final Object key = it.next();
            Collection collisionList = (Collection) classNameToUrlsMap.get(key);
            map.put(key,collisionList.toArray(new URL[collisionList.size()]));
        }
        return map;
    }

    public Map getUrlToClassNameMap() {
        return urlToClassNameMap;
    }

}
