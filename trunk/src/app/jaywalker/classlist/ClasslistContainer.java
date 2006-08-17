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

import java.net.URL;

public class ClasslistContainer extends ClasslistElement {
    protected URL [] urls = new URL[0];
	protected String packageName;

    public ClasslistContainer(URL url) {
        super(url);
    }

    public ClasslistElement [] getClasslistElements() {
        ClasslistElement [] elements = new ClasslistElement[urls.length];
        ClasslistElementFactory factory = new ClasslistElementFactory();
        for (int i = 0; i < urls.length; i++) {
            elements[i] = factory.create(urls[i]);
        }
        return elements;
    }

	public String getPackageName() {
		return packageName;
	}

	protected String toPackageName(URL[] urls) {
		for (int i = 0; i < urls.length; i++) {
			if (urls[i].toString().endsWith(".class")) {
				ClasslistElementFactory factory = new ClasslistElementFactory();
				ClassElement classElement = (ClassElement) factory
						.create(urls[i]);
				return classElement.getPackageName();
			}
		}
		return null;
	}
}
