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

public class HashCode {
    public static long encode(String filename) {
        if (filename == null) {
            return 0;
        }
        int length = filename.length();
        long encoded = 17;
        for (int i = 0; i < length; i++) {
            encoded = encoded * 97 + filename.charAt(i);
        }
        return encoded;
    }
}
