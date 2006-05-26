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
import jaywalker.util.Outputter;

public class ReportSetupMap {

	private final static CollectionHelper HELPER_COLLECTION = new CollectionHelper();

	private Map map = new HashMap();

	private static class Entry {
		private final Tag xmlTag;

		private final Outputter[] htmlTransformers;

		public Entry(Tag xmlTag, Outputter[] htmlTransformer) {
			this.xmlTag = xmlTag;
			this.htmlTransformers = htmlTransformer;
		}

		public Outputter[] getHtmlTransformers() {
			return htmlTransformers;
		}

		public Tag getXmlTag() {
			return xmlTag;
		}
	}

	private interface Visitor {
		public void visit(Entry entry);
	}

	public Tag getXmlTag(String type, String value) {
		return lookupEntry(type, value).getXmlTag();
	}

	private Entry lookupEntry(String type, String value) {
		return (Entry) map.get(toKey(type, value));
	}

	public void put(String type, String value, Tag xmlTag, Outputter htmlTag) {
		map.put(toKey(type, value), new Entry(xmlTag,
				new Outputter[] { htmlTag }));
	}

	public void put(String type, String value, Tag xmlTag, Outputter[] htmlTags) {
		map.put(toKey(type, value), new Entry(xmlTag, htmlTags));
	}

	public String toKey(String type, String value) {
		return type + "," + value;
	}

	public Tag[] getXmlTags(Properties properties) {
		final List xmlTagList = new LinkedList();
		final Visitor visitor = new Visitor() {
			public void visit(Entry entry) {
				xmlTagList.add(entry.getXmlTag());
			}
		};
		accept(properties, visitor);
		return (Tag[]) xmlTagList.toArray(new Tag[xmlTagList.size()]);
	}

	public Outputter[] getHtmlTransformers(Properties properties) {
		final List htmlTransformerList = new LinkedList();
		final Visitor visitor = new Visitor() {
			public void visit(Entry entry) {
				Outputter[] transformers = entry.getHtmlTransformers();
				for (int i = 0; i < transformers.length; i++) {
					htmlTransformerList.add(transformers[i]);
				}
			}
		};
		accept(properties, visitor);
		return (Outputter[]) htmlTransformerList
				.toArray(new Outputter[htmlTransformerList.size()]);
	}

	private void accept(Properties properties, Visitor visitor) {
		Set keySet = properties.keySet();
		String[] keys = HELPER_COLLECTION.toStrings(keySet);
		for (int i = 0; i < keys.length; i++) {
			String[] values = properties.getProperty(keys[i]).split(",");
			for (int j = 0; j < values.length; j++) {
				final Entry entry = lookupEntry(keys[i], values[j]);
				if (entry != null)
					visitor.visit(entry);
			}
		}
	}

	public String[] getKeys() {
		Set keySet = map.keySet();
		return HELPER_COLLECTION.toStrings(keySet);
	}

}
