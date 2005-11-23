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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ImmutableStringSet {
    private final long [] values;

    public ImmutableStringSet() {
        values = new long[0];
    }

    public ImmutableStringSet(String [] strings) {
        if (strings == null || strings.length == 0) {
            values = new long[0];
        } else {
            values = new long[strings.length];
            for (int i = 0; i < strings.length; i++) {
                values[i] = HashCode.encode(strings[i]);
            }
            Arrays.sort(values);
        }
    }

    public boolean contains(String value) {
        return indexOf(value) >= 0;
    }

    public int indexOf(String value) {
        if (value == null) {
            return -1;
        }
        return Arrays.binarySearch(values, HashCode.encode(value));
    }

    public int size() {
        return values.length;
    }
}
