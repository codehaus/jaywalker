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
import java.util.HashMap;
import java.util.Map;

import jaywalker.util.URLHelper;

public class ClasslistElementStatistic implements ClasslistElementListener {

	private class Counter {
		private long value;

		public void increment() {
			value++;
		}

		public long get() {
			return value;
		}
	}

	private Counter archiveCntr = new Counter();

	private Counter dirCntr = new Counter();

	private Counter fileCntr = new Counter();

	private Map fileCounterMap = new HashMap();

	private Map directoryCounterMap = new HashMap();

	public void classlistElementVisited(ClasslistElementEvent event) {
		final ClasslistElement element = event.getElement();
		final Class clazz = element.getClass();
		if (clazz == ArchiveContainer.class) {
			archiveCntr.increment();
		} else if (clazz == DirectoryContainer.class) {
			dirCntr.increment();
			incrementCounterIfOfType(element.getContainer(),
					directoryCounterMap, DirectoryContainer.class);
		} else {
			fileCntr.increment();
			incrementCounterIfOfType(element.getContainer(), fileCounterMap,
					DirectoryContainer.class);
			// new URLHelper().toBaseContainerUrl(element.getURL());
		}
	}

	private void incrementCounterIfOfType(ClasslistElement element, Map map,
			Class clazz) {
		if (element.getClass() == clazz) {
			Counter counter = (Counter) map.get(element.getURL());
			if (counter == null) {
				counter = new Counter();
				map.put(element.getURL(), counter);
			}
			counter.increment();
		}
	}

	public void lastClasslistElementVisited() {
	}

	public String toString() {
		return archiveCntr + " archives, " + dirCntr + " directories, "
				+ fileCntr + " files";
	}

	public long getFileCountFor(URL url) {
		Counter counter = (Counter) fileCounterMap.get(url);
		return counter.get();
	}

	public long getDirectoryCountFor(URL url) {
		Counter counter = (Counter) directoryCounterMap.get(url);
		return counter.get();
	}
}
