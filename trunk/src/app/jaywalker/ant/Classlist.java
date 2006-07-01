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
package jaywalker.ant;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;

public class Classlist extends FileSet {
	public static final String TYPE_DEEP = "deep";

	public static final String TYPE_SHALLOW = "shallow";

	public static final String TYPE_SYSTEM = "system";

	public static final String TYPE_DEFAULT = TYPE_DEEP;

	public static final String[] LEGAL_TYPES = new String[] { TYPE_DEEP,
			TYPE_SHALLOW, TYPE_SYSTEM };

	private String type = TYPE_DEFAULT;

	private String nestedPath;

	public String getType() {
		return type;
	}

	public void setType(String value) {
		type = toValidType(value);
	}

	private String toValidType(String value) {
		for (int i = 0; i < LEGAL_TYPES.length; i++) {
			if (LEGAL_TYPES[i].equals(value)) {
				return LEGAL_TYPES[i];
			}
		}
		return TYPE_DEFAULT;
	}

	public void setFile(File file) {
		String path = file.getAbsolutePath();
		if (path.indexOf("!") == -1) {
			super.setFile(file);
		}
		nestedPath = file.getAbsolutePath();
	}

	public String getNestedPath() {
		return nestedPath;
	}
}