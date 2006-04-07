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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jaywalker.util.FileSystem;
import jaywalker.util.URLHelper;
import jaywalker.util.ZipFileVisitor;
import jaywalker.util.ZipFileVisitor.ZipEntryListener;

public class ArchiveExpander {

	public void expand(URL url) throws IOException {
		ArchiveCache cache = new ArchiveCache(url);
		if (cache.isStale()) {
			cache.delete();
		}
		if (cache.isMissing()) {
			cache.lock();
			writeToCache(cache);
			cache.unlock();
		}
	}

	private void writeToCache(final ArchiveCache cache) throws IOException {
		URLHelper helper = new URLHelper();
		File archiveFile = cache.toArchiveFile();
		File archiveIdx = helper.toArchiveIdx(cache.getURL());
		File archiveCls = helper.toArchiveCls(cache.getURL());

		final String absolutePath = archiveFile.getAbsolutePath();

		// Look at archive...
		ZipFile zip = new ZipFile(absolutePath);

		final Properties idxProperties = new Properties();
		final Properties clsProperties = new Properties();

		ZipFileVisitor visitor = new ZipFileVisitor(zip);
		visitor.addListener(createZipEntryListener(cache, idxProperties,
				clsProperties));
		visitor.accept();

		idxProperties.store(new FileOutputStream(archiveIdx), absolutePath);
		clsProperties.store(new FileOutputStream(archiveCls), absolutePath);

		zip.close();

	}

	private ZipEntryListener createZipEntryListener(final ArchiveCache cache,
			final Properties idxProperties, final Properties clsProperties) {
		return new ZipEntryListener() {
			public void visit(ZipFile zipFile, ZipEntry zipEntry)
					throws IOException {
				String filename = zipEntry.getName();
				File cachedFile = cache.createFile(filename);

				if (!zipEntry.isDirectory()) {
					FileSystem.writeInputStreamToFile(zipFile.getInputStream(zipEntry),
							zipEntry.getSize(), cachedFile);
					if (filename.endsWith(".class")) {
						ClassElementFile classElementFile = new ClassElementFile(
								cachedFile.toURL());
						clsProperties.setProperty(filename, classElementFile
								.toPropertyValue());
					}
				}
				idxProperties.setProperty(filename, cachedFile
						.getAbsolutePath());
			}
		};
	}
}
