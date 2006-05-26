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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import jaywalker.classlist.ArchiveExpander;
import jaywalker.classlist.JayWalkerTestCase;
import jaywalker.testutil.Path;

public class DirectoryListingTest extends JayWalkerTestCase {
	public void testShouldReturnTopLevelElementUrls() throws IOException,
			URISyntaxException {

		// Clean up any temporary files
		final URL url = Path.FILE_TEST1_JAR.toURL();
		final File expandedURL = new URLHelper().toEncodedFile(new URL("jar:"
				+ url.toString() + "!/"));
		FileSystem.delete(expandedURL);

		// Expand the archive into the temporary directory
		// Show that the classlist element's file representation exists
		ArchiveExpander expander = new ArchiveExpander();
		expander.expand(url);
		URLHelper helper = new URLHelper();
		File fileIdx = helper.toArchiveIdx(url);
		DirectoryListing listing = new DirectoryListing(url, fileIdx);
		URL[] urls = listing.toUrls(url);
		assertEquals(6, urls.length);
		for (int i = 0; i < urls.length; i++) {
			final int length = listing.toUrls(urls[i]).length;
			if (urls[i].toString().endsWith("/META-INF/")) {
				assertEquals(1, length);
			} else {
				assertEquals(0, length);
			}
		}

	}

}
