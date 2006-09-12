/**
 * Copyright 2005 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jaywalker.report;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jaywalker.util.CollectionHelper;

public class ReportSetupMap {

	private final static CollectionHelper HELPER_COLLECTION = new CollectionHelper();

	private Map map = new HashMap();

	private interface Visitor {
		public void visit(Tag tag);
	}

	public Tag getXmlTag(String type, String value) {
		return lookupTag(type, value);
	}

	private Tag lookupTag(String type, String value) {
		return (Tag) map.get(toKey(type, value));
	}

	public void put(String type, String value, Tag xmlTag) {
		map.put(toKey(type, value), xmlTag);
	}

	public String toKey(String type, String value) {
		return type + "," + value;
	}

	public Tag[] getXmlTags(Properties properties) {
		final List xmlTagList = new LinkedList();
		final Visitor visitor = new Visitor() {
			public void visit(Tag tag) {
				xmlTagList.add(tag);
			}
		};
		accept(properties, visitor);
		return (Tag[]) xmlTagList.toArray(new Tag[xmlTagList.size()]);
	}

	private void accept(Properties properties, Visitor visitor) {
		Set keySet = properties.keySet();
		String[] keys = HELPER_COLLECTION.toStrings(keySet);
		for (int i = 0; i < keys.length; i++) {
			String[] values = properties.getProperty(keys[i]).split(",");
			for (int j = 0; j < values.length; j++) {
				final Tag tag = lookupTag(keys[i], values[j]);
				if (tag != null)
					visitor.visit(tag);
			}
		}
	}

	public String[] getKeys() {
		Set keySet = map.keySet();
		return HELPER_COLLECTION.toStrings(keySet);
	}

}
