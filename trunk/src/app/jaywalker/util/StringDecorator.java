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
package jaywalker.util;

public class StringDecorator {

	public final static String EMPTY = "";

	private final String value;

	public StringDecorator(String value) {
		this.value = value;
	}

	public String appendIfMissing(String suffix) {
		if (value == null)
			return suffix;
		if (value.endsWith(suffix))
			return value;
		return value + suffix;
	}

	public String prependIfMissing(String prefix) {
		if (value == null)
			return prefix;
		if (value.startsWith(prefix))
			return value;
		return prefix + value;
	}

	private boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public boolean isEmpty() {
		return isEmpty(value);
	}

	public String trim() {
		return (value == null) ? null : value.trim();
	}

	public String capitalize() {
		return Character.toUpperCase(value.charAt(0)) + value.substring(1);
	}

	public String substringBeforeLast(String delimiter) {
		return substringBeforeLast(delimiter, value);
	}

	public String substringAfterLast(String delimiter) {
		if (isEmpty()) {
			return value;
		}
		if (isEmpty(delimiter)) {
			return EMPTY;
		}
		int idx = value.lastIndexOf(delimiter);
		if (idx == -1 || idx == (value.length() - delimiter.length())) {
			return EMPTY;
		}
		return value.substring(idx + delimiter.length());
	}

	public boolean isBlank() {
		int length;
		if (value == null || (length = value.length()) == 0) {
			return true;
		}
		for (int i = 0; i < length; i++) {
			if ((!Character.isWhitespace(value.charAt(i)))) {
				return false;
			}
		}
		return true;
	}

	public String extractCommonPrefix(String other) {
		StringBuffer sb = new StringBuffer();
		int length = (value.length() < other.length()) ? value.length() : other
				.length();
		for (int i = 0; i < length; i++) {
			final char ch = value.charAt(i);
			if (ch == other.charAt(i)) {
				sb.append(ch);
			} else {
				return sb.toString();
			}
		}
		return sb.toString();
	}

	public String substringBeforeLast(String delimiter, String defaultString) {
		if (isEmpty() || isEmpty(delimiter)) {
			return defaultString;
		}
		int idx = value.lastIndexOf(delimiter);
		if (idx == -1) {
			return defaultString;
		}
		return value.substring(0, idx);
	}

	public String spaceAndReplace(int spaceCnt, String delimiter,
			String newDelimiter) {
		StringBuffer sb = new StringBuffer();
		String[] values = value.split(delimiter);
		for (int i = 0; i < values.length; i++) {
			sb.append(toSpaces(spaceCnt));
			sb.append(values[i]);
			sb.append(newDelimiter);
		}
		return sb.toString();
	}

	private String toSpaces(int cnt) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cnt; i++) {
			sb.append(' ');
		}
		return sb.toString();
	}
}
