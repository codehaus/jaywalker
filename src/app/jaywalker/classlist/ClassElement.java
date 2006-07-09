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

import java.io.IOException;
import java.net.URL;

import jaywalker.util.URLHelper;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

public class ClassElement extends ClasslistElement {
	private ClassElementFile file;

	private JavaClass javaClass;

	public static class Creator implements ClasslistElementCreator {
		public boolean isType(URL url) {
			return url.toString().endsWith(".class");
		}

		public ClasslistElement create(URL url) {
			return new ClassElement(url);
		}
	}

	public ClassElement(URL url) {
		super(url);
	}

	public String getName() {
		return getClassElementFile().getClassName();
	}

	public String getPackageName() {
		String name = getName();
		int idx = name.lastIndexOf('.');
		if (idx != -1)
			return name.substring(0, idx);
		return "";
	}

	public String getSuperName() {
		return getClassElementFile().getSuperClassName();
	}

	public String[] getInterfaceNames() {
		return getClassElementFile().getInterfaceNames();
	}

	public String[] getDependencies() {
		return file.getDependencies();
	}

	private ClassElementFile getClassElementFile() {
		if (file == null) {
			file = new ClassElementFile(url);
		}
		return file;
	}

	public JavaClass getJavaClass() {
		try {
			if (javaClass == null) {
				URLHelper urlHelper = new URLHelper();
				if (urlHelper.isArchivedFile(url)) {
					URL baseUrl = urlHelper.toBaseContainerUrl(url);
					ArchiveCache cache = new ArchiveCache(baseUrl);
					String fileName = url.toString();
					fileName = fileName
							.substring(fileName.lastIndexOf("!/") + 2);
					javaClass = cache.toJavaClass(fileName);
				} else {
					javaClass = new ClassParser(url.openStream(), url
							.toString()).parse();
				}
			}
			return javaClass;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getType() {
		ClassElementFile classElementFile = getClassElementFile();
		if (classElementFile.isInterface()) {
			return "interface";
		}
		if (classElementFile.isAbstract()) {
			return "abstract";
		}
		return "class";
	}

}
