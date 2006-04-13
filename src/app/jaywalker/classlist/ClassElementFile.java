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
import java.io.InputStream;
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

	private final URL url;

	private String className;

	private String superClassName;

	private String[] interfaces;

	private String[] dependencies;

	public ClassElementFile(URL url, JavaClass javaClass) {
		this.url = url;
		initializeWith(javaClass);
	}

	public ClassElementFile(URL baseUrl) {
		this.url = baseUrl;
		URLHelper helper = new URLHelper();
		try {
			URL adjustedUrl = helper.toLegalArchiveUrl(url);
			if (adjustedUrl != null) {
				Map classMap = lookupClassMap(adjustedUrl);
				initializeWith(classMap);
			} else {
				JavaClass javaClass = new ClassParser(url.openStream(), baseUrl
						.toString()).parse();
				initializeWith(javaClass);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ClassElementFile(InputStream inputStream, URL baseUrl) {
		this.url = baseUrl;
		try {
			JavaClass javaClass = new ClassParser(inputStream, baseUrl
					.toString()).parse();
			initializeWith(javaClass);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeWith(JavaClass javaClass) {
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

	private void initializeWith(Map classMap) {
		String classInfo = (String) classMap.get(url);
		int idx = 0;
		int nextIdx = toNextIdx(classInfo, idx);
		className = classInfo.substring(0, nextIdx);
		idx = nextIdx + 1;
		nextIdx = toNextIdx(classInfo, idx);
		superClassName = classInfo.substring(idx, nextIdx);
		idx = nextIdx + 1;
		interfaces = split(classInfo, idx);
		idx = skipNextList(classInfo, idx);
		dependencies = split(classInfo, idx);
	}

	private Map lookupClassMap(URL url) throws IOException {
		URLHelper helper = new URLHelper();
		final String key = "cl:" + url.toString();
		Map map;
		final ResourceLocator locator = ResourceLocator.instance();

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
		return className;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public String[] getInterfaceNames() {
		return interfaces;
	}

	public String[] getDependencies() {
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

	public String toPropertyValue() {
		StringBuffer sb = new StringBuffer();
		sb.append(className).append("|");
		sb.append(superClassName).append("|");
		sb.append(denormalize(interfaces, "|"));
		sb.append(denormalize(dependencies, "|"));
		return sb.toString();
	}

	private String denormalize(String[] values, String delimiter) {
		StringBuffer sb = new StringBuffer();
		sb.append(values.length).append(delimiter);
		for (int i = 0; i < values.length; i++) {
			sb.append(values[i]).append(delimiter);
		}
		return sb.toString();
	}

}
