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

import java.util.Properties;

public class Option {
	private String name;

	private String value;

	public Option() {
	}

	public Option(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String[] splitValue(String delimiter) {
		String[] values = value.split(",");
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i].trim();
		}
		return values;
	}

	public int hashCode() {
		int value = 17;
		value = 37 * value + ((name != null) ? name.hashCode() : 0);
		return value;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		final Option that = (Option) o;

		return name.equals(that.name);
	}

	public static Properties toProperties(Option[] options) {
		Properties properties = new Properties();
		for (int i = 0; i < options.length; i++) {
			properties.put(options[i].getName(), options[i].getValue());
		}
		return properties;
	}

}
