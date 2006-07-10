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

public class ClasslistElementStatistic implements ClasslistElementListener {

	private class Counter {
		private long value;

		public void increment() {
			value++;
		}

		public long get() {
			return value;
		}
		
		public String toString() {
			return String.valueOf(value);
		}
	}

	private Counter archiveCntr = new Counter();

	private Counter dirCntr = new Counter();

	private Counter fileCntr = new Counter();

	public void classlistElementVisited(ClasslistElementEvent event) {
		final ClasslistElement element = event.getElement();
		final Class clazz = element.getClass();
		if (clazz == ArchiveContainer.class) {
			archiveCntr.increment();
		} else if (clazz == DirectoryContainer.class) {
			dirCntr.increment();
		} else {
			fileCntr.increment();
		}
	}

	public void lastClasslistElementVisited() {
	}

	public String toString() {
		return archiveCntr + " archives, " + dirCntr + " directories, "
				+ fileCntr + " files";
	}

}
