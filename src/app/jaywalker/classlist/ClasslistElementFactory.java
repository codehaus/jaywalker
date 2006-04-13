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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jaywalker.util.ResourceLocator;
import jaywalker.util.ThreadHelper;

public class ClasslistElementFactory {

	private final static ClasslistElementCreator[] CREATORS = new ClasslistElementCreator[] {
			new ClassElement.Creator(), new DirectoryContainer.Creator(),
			new ArchiveContainer.Creator(), new FileElement.Creator() };

	public ClasslistElement create(URL url) {
		return findAndCreate(url);
	}

	private ClasslistElement findAndCreate(URL url) {
		new ThreadHelper().verify(url);
		for (int i = 0; i < CREATORS.length; i++) {
			if (CREATORS[i].isType(url)) {
				return CREATORS[i].create(url);
			}
		}
		throw new IllegalArgumentException(
				"URL doesn't map to any known file types: " + url);
	}

	public ClasslistElement[] create(String classlist) {
		StringTokenizer st = new StringTokenizer(classlist, File.pathSeparator);
		List list = new ArrayList();

		while (st.hasMoreTokens()) {
			try {
				list.add(create(new File(st.nextToken()).toURL()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		return (ClasslistElement[]) list.toArray(new ClasslistElement[list
				.size()]);
	}
}
