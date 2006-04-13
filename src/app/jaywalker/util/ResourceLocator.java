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

import java.util.HashMap;
import java.util.Map;

public class ResourceLocator {
	private final static ResourceLocator INSTANCE = new ResourceLocator();

	private Map map = new HashMap();

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
			return value;
		}
	}

	public void register(String key, Object value) {
		map.put(key, value);
	}

	public void clear() {
		map.clear();
	}

	public void clear(String key) {
		map.remove(key);
	}
}
