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

public class StringHelper {
    public static final String EMPTY = "";

    public String appendIfMissing(String suffix, String target) {
        if (target == null) return suffix;
        if (target.endsWith(suffix)) return target;
        return target + suffix;
    }

    public String prependIfMissing(String prefix, String target) {
        if (target == null) return prefix;
        if (target.startsWith(prefix)) return target;
        return prefix + target;
    }

    public boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public String substringBeforeLast(String str, String delimiter) {
        if (isEmpty(str) || isEmpty(delimiter)) {
            return str;
        }
        int idx = str.lastIndexOf(delimiter);
        if (idx == -1) {
            return str;
        }
        return str.substring(0, idx);
    }

    public String substringAfterLast(String str, String delimiter) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(delimiter)) {
            return EMPTY;
        }
        int idx = str.lastIndexOf(delimiter);
        if (idx == -1 || idx == (str.length() - delimiter.length())) {
            return EMPTY;
        }
        return str.substring(idx + delimiter.length());
    }

    public boolean isBlank(String str) {
        int length;
        if (str == null || (length = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if ((!Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

}
