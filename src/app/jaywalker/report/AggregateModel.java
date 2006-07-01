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

import jaywalker.classlist.ClasslistElementEvent;
import jaywalker.classlist.ClasslistElementListener;

public class AggregateModel implements ClasslistElementListener {
	private final ClasslistElementListener[] models;

	public AggregateModel(ClasslistElementListener[] listeners) {
		models = listeners;
	}

	public void classlistElementVisited(ClasslistElementEvent event) {
		for (int i = 0; i < models.length; i++) {
			models[i].classlistElementVisited(event);
		}
	}

	public void lastClasslistElementVisited() {
		for (int i = 0; i < models.length; i++) {
			models[i].lastClasslistElementVisited();
		}
	}

}
