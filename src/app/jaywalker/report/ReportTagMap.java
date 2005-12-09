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
package jaywalker.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ReportTagMap {
	private Map map = new HashMap();

	public ReportTag get(String type, String value) {
		return (ReportTag) map.get(toKey(type, value));
	}

	public void put(String type, String value, ReportTag reportTag) {
		map.put(toKey(type, value), reportTag);
	}

	public String toKey(String type, String value) {
		return type + "," + value;
	}

	public ReportTag[] get(Properties properties) {
		List reportTagList = new ArrayList();
		Set keySet = properties.keySet();
		String[] keys = (String[]) keySet.toArray(new String[keySet.size()]);
		for (int i = 0; i < keys.length; i++) {
			String[] values = properties.getProperty(keys[i]).split(",");
			for (int j = 0; j < values.length; j++) {
				final ReportTag reportTag = get(keys[i], values[j]);
				if (reportTag != null)
					reportTagList.add(reportTag);
			}
		}
		return (ReportTag[]) reportTagList.toArray(new ReportTag[reportTagList
				.size()]);
	}
	
	public String [] getKeys() {
		Set keySet = map.keySet();
		return (String[]) keySet.toArray(new String[keySet.size()]);
	}

}
