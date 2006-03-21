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
package jaywalker.classlist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jaywalker.util.ResourceLocator;
import jaywalker.util.URLHelper;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.JavaClass;

public class ClassElementFile {
	private Map classMap;

	private final ResourceLocator locator = ResourceLocator.instance();

	private final URL url;

	private String className;

	private String superClassName;

	private String[] interfaces;

	private String[] dependencies;

	public ClassElementFile(URL baseUrl) {
		this.url = baseUrl;
		URLHelper helper = new URLHelper();
		try {
			URL adjustedUrl = helper.toLegalArchiveUrl(url);
			if (adjustedUrl != null) {
				classMap = lookupClassMap(adjustedUrl);
			} else {
				JavaClass javaClass = new ClassParser(url.openStream(), baseUrl
						.toString()).parse();
				className = javaClass.getClassName();
				superClassName = javaClass.getSuperclassName();
				interfaces = javaClass.getInterfaceNames();
				Set set = new HashSet();
				DependencyVisitor dependencyVisitor = new DependencyVisitor();
				DescendingVisitor traverser = new DescendingVisitor(javaClass,
						dependencyVisitor);
				traverser.visit();
				Enumeration enumeration = dependencyVisitor.getDependencies();
				while (enumeration.hasMoreElements()) {
					String className = (String) enumeration.nextElement();
					if (!className.equals(javaClass.getClassName())) {
						set.add(className);
					}
				}
				dependencyVisitor.clearDependencies();
				dependencies = (String[]) set.toArray(new String[set.size()]);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Map lookupClassMap(URL url) throws IOException {
		URLHelper helper = new URLHelper();

		final String key = "cl:" + url.toString();
		Map map;
		if (!locator.contains(key)) {
			map = createClassMap(url, helper.toArchiveCls(url));
			locator.register(key, map);
		} else {
			map = (Map) locator.lookup(key);
		}
		return map;
	}

	private Map createClassMap(URL baseUrl, File fileCls) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(fileCls));
		final String[] filenames = extractKeys(properties);
		URL[] urls = new URLHelper().toURLs(baseUrl, filenames);
		Map map = new HashMap();
		for (int i = 0; i < filenames.length; i++) {
			map.put(urls[i], properties.getProperty(filenames[i]));
		}
		return map;
	}

	private String[] extractKeys(Properties properties) {
		String[] decodedFilenames = (String[]) properties.keySet().toArray(
				new String[properties.size()]);
		Arrays.sort(decodedFilenames);
		return decodedFilenames;
	}

	public String getClassName() {
		if (className == null) {
			String classInfo = (String) classMap.get(url);
			int idx = 0;
			className = classInfo.substring(0, toNextIdx(classInfo, idx));
		}
		return className;
	}

	public String getSuperClassName() {
		if (superClassName == null) {
			String classInfo = (String) classMap.get(url);
			int idx = 0;
			idx = toNextIdx(classInfo, idx) + 1;
			superClassName = classInfo
					.substring(idx, toNextIdx(classInfo, idx));
		}
		return superClassName;
	}

	public String[] getInterfaceNames() {
		if (interfaces == null) {
			String classInfo = (String) classMap.get(url);
			int idx = 0;
			idx = toNextIdx(classInfo, idx) + 1;
			idx = toNextIdx(classInfo, idx) + 1;
			interfaces = split(classInfo, idx);
		}
		return interfaces;
	}

	public String[] getDependencies() {
		if (dependencies == null) {
			String classInfo = (String) classMap.get(url);
			int idx = 0;
			idx = toNextIdx(classInfo, idx) + 1;
			idx = toNextIdx(classInfo, idx) + 1;
			idx = skipNextList(classInfo, idx);
			dependencies = split(classInfo, idx);
		}
		return dependencies;
	}

	private int skipNextList(String classInfo, int idx) {
		int nextIdx = toNextIdx(classInfo, idx);
		int cnt = Integer.parseInt(classInfo.substring(idx, nextIdx));
		idx = nextIdx + 1;
		for (int i = 0; i < cnt; i++) {
			nextIdx = toNextIdx(classInfo, idx);
			idx = nextIdx + 1;
		}
		return idx;
	}

	private String[] split(String classInfo, int idx) {
		int nextIdx = toNextIdx(classInfo, idx);
		int cnt = Integer.parseInt(classInfo.substring(idx, nextIdx));
		idx = nextIdx + 1;
		String[] strings = new String[cnt];
		for (int i = 0; i < cnt; i++) {
			nextIdx = toNextIdx(classInfo, idx);
			strings[i] = classInfo.substring(idx, nextIdx);
			idx = nextIdx + 1;
		}
		return strings;
	}

	private int toNextIdx(String classInfo, int idx) {
		int endIdx = classInfo.indexOf('|', idx);
		if (endIdx == -1)
			endIdx = classInfo.length();
		return endIdx;
	}
}
