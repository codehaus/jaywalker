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

import jaywalker.util.URLHelper;

public class ClasslistElement {
	protected final URL url;

	public ClasslistElement(URL url) {
		this.url = url;
	}

	public URL getURL() {
		return url;
	}

	public ClasslistContainer getContainer() {
		URL parentUrl = new URLHelper().toParentURL(url);
		if (parentUrl == null)
			return null;
		return (ClasslistContainer) new ClasslistElementFactory()
				.create(parentUrl);
	}

	public String toString() {
		return url.toString();
	}

	public String getType() {
		return "unknown";
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		final ClasslistElement that = (ClasslistElement) o;

		return url.equals(that.url);

	}

	public int hashCode() {
		return url.hashCode();
	}

}
