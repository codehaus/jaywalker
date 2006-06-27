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

import java.io.IOException;
import java.net.URL;

import jaywalker.util.ResourceLocator;

public class ClasslistElementVisitor {

	private final static int DEFAULT_CAPACITY = 5;

	private final ClasslistElement[] classlistElements;

	private int lastIdx = 0;

	private ClasslistElementListener[] listeners = new ClasslistElementListener[0];

	private final URL[] ignoreClasslistElements;

	public ClasslistElementVisitor(ClasslistElement[] classlistElements,
			URL[] ignoreClasslistElements) {
		this.classlistElements = classlistElements;
		this.ignoreClasslistElements = ignoreClasslistElements;
	}
	
	public ClasslistElementVisitor(ClasslistElement[] classlistElements) {
		this.classlistElements = classlistElements;
		this.ignoreClasslistElements = new URL[0];
	}

	public ClasslistElementVisitor(ClasslistContainer cl) {
		this.classlistElements = cl.getClasslistElements();
		this.ignoreClasslistElements = new URL[0];
	}

	public void accept() throws IOException {
		accept(this);
		fireLastClasslistElementVisited();
	}

	public void accept(ClasslistElementVisitor v) throws IOException {
		for (int i = 0; i < classlistElements.length; i++) {
			if (shouldVisit(classlistElements[i])) {
				v.visit(classlistElements[i]);
			}
		}
	}

	private boolean shouldVisit(ClasslistElement element) {
		if (ignoreClasslistElements.length == 0) {
			return true;
		}
		for (int i = 0; i < ignoreClasslistElements.length; i++) {
			System.out.println(ignoreClasslistElements[i]);
			if (element.getURL().toString().indexOf(
					ignoreClasslistElements[i].toString()) != -1) {
				return false;
			}
		}
		return true;
	}

	public void visit(ClasslistElement cle) throws IOException {

		notifyListeners(cle);

		if (cle instanceof ClasslistContainer) {
			new ClasslistElementVisitor((ClasslistContainer) cle).accept(this);
			clearTemporaryStateFor((ClasslistContainer) cle);
		}

	}

	private void clearTemporaryStateFor(ClasslistContainer clc) {
		final ResourceLocator locator = ResourceLocator.instance();
		URL url = clc.getURL();
		locator.unregister("directoryListing:" + url);
		locator.unregister("classInfoByUrlMap:" + url);
	}

	private void notifyListeners(ClasslistElement element) {
		ClasslistElementEvent event = new ClasslistElementEvent(element);
		for (int i = 0; i < lastIdx; i++) {
			listeners[i].classlistElementVisited(event);
		}
	}

	public void addListener(ClasslistElementListener listener) {
		ensureCapacity();
		listeners[lastIdx++] = listener;
	}

	private void ensureCapacity() {
		initListeners();
		if (lastIdx + 1 >= listeners.length - 1) {
			resizeListenersByFactor(2);
		}
	}

	private void resizeListenersByFactor(int factor) {
		ClasslistElementListener[] newListeners = new ClasslistElementListener[listeners.length
				* factor];
		System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
		listeners = newListeners;
	}

	private void initListeners() {
		if (0 == listeners.length) {
			listeners = new ClasslistElementListener[DEFAULT_CAPACITY];
			lastIdx = 0;
		}
	}

	private void fireLastClasslistElementVisited() {
		for (int i = 0; i < lastIdx; i++) {
			listeners[i].lastClasslistElementVisited();
		}
	}

	public void removeAllListeners() {
		for (int i = 0; i < listeners.length; i++) {
			listeners[i] = null;
		}
		listeners = new ClasslistElementListener[0];
		lastIdx = 0;
	}

}
