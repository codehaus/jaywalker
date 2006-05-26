/**
 * Copyright 2005 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jaywalker.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResourceLocator {
	private final static ResourceLocator INSTANCE = new ResourceLocator();

	protected Map map = new HashMap();

	protected Map statisticsMap = new HashMap();

	public static ResourceLocator instance() {
		return INSTANCE;
	}

	public boolean contains(String key) {
		return map.containsKey(key);
	}

	public Object lookup(String key) {
		Object value = map.get(key);
		if (value == null) {
			throw new ResourceNotFoundException("Could not find resource: "
					+ key);
		} else {
			incrementLookupCount(key);
			return value;
		}
	}

	private void incrementLookupCount(String key) {
		Long lookupCount = (Long) statisticsMap.get(key);
		if (lookupCount == null) {
			lookupCount = new Long(1);
		} else {
			lookupCount = new Long(lookupCount.longValue() + 1);
		}
		statisticsMap.put(key, lookupCount);
	}

	public void register(String key, Object value) {
		map.put(key, value);
	}
	
	public Object unregister(String key) {
		return map.remove(key);
	}

	public void clear() {
		map.clear();
	}

	public void clear(String key) {
		map.remove(key);
	}

	public String toString() {
		final Set keySet = map.keySet();
		StringBuffer sb = new StringBuffer("ResourceLocator:\n");
		for (Iterator it = keySet.iterator(); it.hasNext();) {
			final Object key = it.next();
			sb.append("    ");
			sb.append(key);
			sb.append(" (");
			final Long count = (Long) statisticsMap.get(key);
			sb.append((count == null) ? 0 : count.longValue());
			sb.append(")\n");
		}
		return sb.toString();
	}

	protected Collection findKeysWithMatchingFilterAsPrefix(
			Collection collection, String filter) {
		List filterList = new ArrayList();
		for (Iterator it = collection.iterator(); it.hasNext();) {
			Object key = it.next();
			if (key.toString().startsWith(filter)) {
				filterList.add(key);
			}
		}
		return filterList;
	}

	public Collection unregisterByKeyPrefix(String prefix) {
		Collection keyCollection = findKeysWithMatchingFilterAsPrefix(map
				.keySet(), prefix);
		List list = new ArrayList();
		for (Iterator it = keyCollection.iterator(); it.hasNext();) {
			list.add(map.remove(it.next()));
		}
		return list;
	}
}
