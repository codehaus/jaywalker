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

import jaywalker.classlist.ClassElement;
import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementEvent;
import jaywalker.classlist.ClasslistElementListener;

import java.net.URL;

public class DependencyModel implements ClasslistElementListener {
	private final DependencyHelper dependencyHelper = new DependencyHelper();

	private final DependencyCycleHelper dependencyCycleHelper = new DependencyCycleHelper(
			dependencyHelper);

	public void classlistElementVisited(ClasslistElementEvent event) {
		ClasslistElement classlistElement = event.getElement();
		if (classlistElement.getClass() != ClassElement.class) {
			return;
		}
		
		URL url = classlistElement.getURL();
		if (dependencyHelper.isResolved(url)) {
			return;
		}
		
		ClassElement classElement = (ClassElement) classlistElement;
		String className = classElement.getName();

		dependencyHelper.markAsResolved(url, className);
		String[] dependencies = classElement.getDependencies();
		for (int i = 0; i < dependencies.length; i++) {
			String dependency = asClassName(dependencies[i]);
			if (dependencyHelper.isResolved(dependency)) {
				dependencyHelper.updateResolved(url, dependency);
			} else {
				dependencyHelper.markAsUnresolved(url, dependency);
			}
		}

	}

	public void lastClasslistElementVisited() {
		dependencyHelper.resolveSystemClasses();
	}

	private String asClassName(String dependency) {
		String value = dependency;
		int idx;
		if ((idx = value.lastIndexOf("[")) != -1)
			value = value.substring(idx + 2);
		if (value.endsWith(";"))
			value = value.substring(0, value.length() - 1);
		return value;
	}

	public URL[] lookupResolvedContainerDependencies(URL url) {
		return dependencyHelper.lookupResolvedContainerDependency(url);
	}

	public String[] lookupUnresolvedElementDependencies(URL url) {
		return dependencyHelper.lookupUnresolvedElementDependency(url);
	}

	public URL[] lookupElementCycleDependency(URL url) {
		return dependencyCycleHelper.lookupElementCycleDependency(url);
	}

	public URL[] lookupContainerCyclicDependency(URL url) {
		return dependencyCycleHelper.lookupContainerCycleDependency(url);
	}

	public String[] lookupResolvedPackageNameDependencies(URL url) {
		return dependencyHelper.lookupResolvedPackageNameDependencies(url);
	}

	public URL[] lookupPackageCycleDependency(URL url) {
		return dependencyCycleHelper.lookupPackageCycleDependency(url);
	}

}