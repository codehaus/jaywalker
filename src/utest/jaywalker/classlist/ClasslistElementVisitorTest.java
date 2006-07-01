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

import jaywalker.testutil.Path;
import jaywalker.util.URLHelper;

public class ClasslistElementVisitorTest extends JayWalkerTestCase {
	protected void assertLegalUrl(URL url) {
		assertNotNull(url);
		String urlString = url.toString();
		if (urlString.indexOf("!/") != -1) {
			assertTrue(urlString.startsWith("jar:file:"));
		}
		if (new URLHelper().isLegalArchiveExtension(url)) {
			if (urlString.indexOf("!/") == -1) {
				assertTrue(urlString.startsWith("file:"));
			} else {
				assertTrue(urlString.startsWith("jar:file:"));
			}
		}
	}

	public void testShouldVisitAFile() throws IOException {
		final URL url = Path.FILE_CLASSLIST_ELEMENT_FACTORY_CLASS.toURL();
		ClasslistElementListener listener = new ClasslistElementListener() {
			public void classlistElementVisited(ClasslistElementEvent event) {
				assertEquals(url, event.getElement().getURL());
			}

			public void lastClasslistElementVisited() {
			}
		};
		assertVisit(url, listener);
	}

	protected void assetLegalUrlOnVisit(URL url) throws IOException {
		ClasslistElementListener listener = new ClasslistElementListener() {
			public void classlistElementVisited(ClasslistElementEvent event) {
				assertLegalUrl(event.getElement().getURL());
			}

			public void lastClasslistElementVisited() {
			}
		};
		assertVisit(url, listener);
	}

	public void testShouldVisitFilesInADirectory() throws IOException {
		assetLegalUrlOnVisit(Path.DIR_BUILD_APP.toURL());
	}

	public void testShouldVisitFilesInAnArchive() throws IOException {
		assetLegalUrlOnVisit(Path.FILE_TEST1_JAR.toURL());
	}

	public void testShouldVisitFilesInNestedArchives() throws IOException {
		assetLegalUrlOnVisit(Path.FILE_TEST4_JAR.toURL());
	}

	public void testShouldVisitNestedArchivesInADirectory() throws IOException {
		assetLegalUrlOnVisit(Path.DIR_BUILD_JAR.toURL());
	}

	// public void testBiggie() throws IOException {
	// URL url = new File("C:\\temp\\weblogic").toURL();
	// assetLegalUrlOnVisit(url);
	// }

	public void testShouldGrowModelAsNeeded() throws IOException {
		ClasslistElementVisitor visitor = new ClasslistElementVisitor(
				new ClasslistElement[1]);
		final int listenerCnt = 10;
		executeVisitorWithListenerCnt(visitor, listenerCnt);
		executeVisitorWithListenerCnt(visitor, 0);
		executeVisitorWithListenerCnt(visitor, listenerCnt);

	}

	private void executeVisitorWithListenerCnt(ClasslistElementVisitor visitor,
			final int listenerCnt) throws IOException {
		for (int i = 0; i < listenerCnt; i++) {
			visitor.addListener(new ClasslistElementListenerCounter());
		}
		visitor.accept();
		assertEquals(listenerCnt,
				ClasslistElementListenerCounter.visitedCounter);
		assertEquals(listenerCnt,
				ClasslistElementListenerCounter.lastElementVisitedCounter);
		visitor.removeAllListeners();
		ClasslistElementListenerCounter.visitedCounter = 0;
		ClasslistElementListenerCounter.lastElementVisitedCounter = 0;
	}

	private static class ClasslistElementListenerCounter implements
			ClasslistElementListener {

		static int visitedCounter;

		static int lastElementVisitedCounter;

		public void classlistElementVisited(ClasslistElementEvent event) {
			visitedCounter++;
		}

		public void lastClasslistElementVisited() {
			lastElementVisitedCounter++;
		}

	}

}
