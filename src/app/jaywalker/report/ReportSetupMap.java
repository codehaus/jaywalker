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

import jaywalker.util.XsltTransformer;
import jaywalker.xml.Tag;

public class ReportSetupMap {
	private Map map = new HashMap();

	private static class Entry {
		private final Tag xmlTag;

		private final XsltTransformer htmlTransformer;

		public Entry(Tag xmlTag, XsltTransformer htmlTransformer) {
			this.xmlTag = xmlTag;
			this.htmlTransformer = htmlTransformer;
		}

		public XsltTransformer getHtmlTransformer() {
			return htmlTransformer;
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

	public XsltTransformer getHtmlTransformer(String type, String value) {
		return lookupEntry(type, value).getHtmlTransformer();
	}

	private Entry lookupEntry(String type, String value) {
		return (Entry) map.get(toKey(type, value));
	}

	public void put(String type, String value, Tag xmlTag,
			XsltTransformer htmlTag) {
		map.put(toKey(type, value), new Entry(xmlTag, htmlTag));
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

	public XsltTransformer[] getHtmlTransformers(Properties properties) {
		final List htmlTransformerList = new LinkedList();
		final Visitor visitor = new Visitor() {
			public void visit(Entry entry) {
				htmlTransformerList.add(entry.getHtmlTransformer());
			}
		};
		accept(properties, visitor);
		return (XsltTransformer[]) htmlTransformerList
				.toArray(new XsltTransformer[htmlTransformerList.size()]);
	}

	private void accept(Properties properties, Visitor visitor) {
		Set keySet = properties.keySet();
		String[] keys = (String[]) keySet.toArray(new String[keySet.size()]);
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
		return (String[]) keySet.toArray(new String[keySet.size()]);
	}

}
