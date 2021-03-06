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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jaywalker.util.FileSystem;
import jaywalker.util.JayWalkerRuntime;
import jaywalker.util.URLHelper;
import jaywalker.util.ZipFileVisitor;
import jaywalker.util.ZipFileVisitor.ZipEntryListener;

public class ArchiveExpander {

	public void expand(URL url) throws IOException {
		final ArchiveCache cache = new ArchiveCache(url);
		if (cache.isStale()) {
			cache.delete();
		}
		if (cache.isMissing()) {
			create(cache);
		}
	}

	private void create(final ArchiveCache cache) throws IOException {
		cache.lock();
		writeToCache(cache);
		cache.unlock();
	}

	private void writeToCache(final ArchiveCache cache) throws IOException {
		URLHelper helper = new URLHelper();
		File archiveFile = cache.getArchiveFile();
		File archiveIdx = helper.toArchiveIdx(cache.getURL());
		File archiveCls = helper.toArchiveCls(cache.getURL());
		File archiveVersion = helper.toArchiveVersion(cache.getURL());

		final String absolutePath = archiveFile.getAbsolutePath();

		if (!archiveFile.exists()) {
			throw new FileNotFoundException("File not found : "
					+ archiveFile.getAbsolutePath());
		}
		
		cache.create(archiveVersion);
		createArchiveVersion(archiveVersion);

		// Look at archive...
		ZipFile zip = new ZipFile(absolutePath);

		File file = cache.createFile(absolutePath);
		FileChannel channel = new RandomAccessFile(file, "rw").getChannel();

		final Properties idxProperties = new Properties();
		final Properties clsProperties = new Properties();

		ZipFileVisitor visitor = new ZipFileVisitor(zip);
		visitor.addListener(createZipEntryListener(cache, idxProperties,
				clsProperties, channel));
		visitor.accept();

		idxProperties.store(new FileOutputStream(archiveIdx), absolutePath);
		clsProperties.store(new FileOutputStream(archiveCls), absolutePath);

		channel.close();

		zip.close();

	}

	private void createArchiveVersion(File archiveVersion) {
		try {
			Properties properties = new Properties();
			properties.setProperty("version", new JayWalkerRuntime()
					.getVersion());
			properties.store(new FileOutputStream(archiveVersion),
					archiveVersion.getAbsolutePath());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private ZipEntryListener createZipEntryListener(final ArchiveCache cache,
			final Properties idxProperties, final Properties clsProperties,
			final FileChannel outputChannel) {
		return new ZipEntryListener() {
			private long offset;

			private URLHelper urlHelper = new URLHelper();

			public void visit(ZipFile zipFile, ZipEntry zipEntry)
					throws IOException {
				String filename = zipEntry.getName();

				final long size = zipEntry.getSize();
				if (!zipEntry.isDirectory()) {

					if (urlHelper.isLegalArchiveExtension(zipEntry.getName())) {
						File cachedFile = cache.createFile(filename);
						FileSystem.writeInputStreamToFile(zipFile
								.getInputStream(zipEntry), size, cachedFile);
					}

					InputStream inputStream = zipFile.getInputStream(zipEntry);
					ReadableByteChannel inputChannel = Channels
							.newChannel(inputStream);
					outputChannel.transferFrom(inputChannel, offset, size);
					inputChannel.close();

					if (filename.endsWith(".class")) {
						ClassElementFile classElementFile = new ClassElementFile(
								zipFile.getInputStream(zipEntry), new URL(
										"http://localhost"));
						clsProperties.setProperty(filename, classElementFile
								.toPropertyValue());
					}
				}
				idxProperties.setProperty(filename, size + "," + offset);
				offset += size;
			}

		};
	}

}
