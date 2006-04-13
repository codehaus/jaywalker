package jaywalker.classlist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Properties;

import jaywalker.util.FileSystem;
import jaywalker.util.HashCode;
import jaywalker.util.ResourceLocator;
import jaywalker.util.URLHelper;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

public class ArchiveCache {

	private File archiveDir;

	private File archiveFile;

	private File archiveLock;

	private final URL url;

	public ArchiveCache(URL url) throws MalformedURLException {
		this.url = url;
		URLHelper urlHelper = new URLHelper();
		archiveDir = urlHelper.toArchiveDir(url);
		archiveFile = urlHelper.toArchiveFile(url);
		archiveLock = urlHelper.toArchiveLock(archiveDir);
	}

	public boolean isStale() {
		return archiveDir.exists()
				&& (archiveDir.lastModified() <= archiveFile.lastModified() || archiveLock
						.exists());
	}

	public void delete() {
		FileSystem.delete(archiveDir);
	}

	public boolean isMissing() {
		return !archiveDir.exists();
	}

	public void lock() throws IOException {
		archiveLock.createNewFile();
	}

	public void unlock() {
		archiveLock.delete();
	}

	public File createFile(String filename) throws IOException {
		File file = encodeFilename(filename);
		create(file);
		return file;
	}

	public void create(File file) throws IOException {
		file.getParentFile().mkdirs();
		if (file.exists())
			file.delete();
		file.createNewFile();
	}

	private File encodeFilename(String filename) {
		File file = new File(archiveDir, String.valueOf(HashCode
				.encode(filename)));
		return file;
	}

	public File getArchiveFile() {
		return archiveFile;
	}

	public URL getURL() {
		return url;
	}

	public static class ByteBufferInputStream extends InputStream {

		private final ByteBuffer buffer;

		private long index;

		private long limit;

		public ByteBufferInputStream(ByteBuffer buffer, long offset, long size) {
			this.buffer = buffer;
			this.index = offset;
			this.limit = offset + size;
		}

		public int read() throws IOException {
			return (index < limit) ? buffer.get((int) index++) & 0xff : -1;
		}

	}

	protected JavaClass toJavaClass(String fileName) {
		try {
			final File encodedFile = encodeFilename(archiveFile
					.getAbsolutePath());
			FileChannel channel = new RandomAccessFile(encodedFile, "r")
					.getChannel();
			Properties idxProperties = loadIdxPropertiesFromUrl(url);
			String value = idxProperties.getProperty(fileName);
			String[] values = value.split(",");
			final long size = Long.parseLong(values[0]);
			final long index = Long.parseLong(values[1]);
			JavaClass javaClass = toJavaClass(fileName, channel, index, size);
			channel.close();
			return javaClass;
		} catch (FileNotFoundException e) {
			throw new ArchiveCacheException(
					"Encoded archiveFile was not found : "
							+ archiveFile.getAbsolutePath(), e);
		} catch (IOException e) {
			throw new ArchiveCacheException(
					"IOException while closing channel", e);
		}

	}

	protected JavaClass toJavaClass(String fileName, FileChannel channel,
			long index, long size) {
		try {
			ByteBuffer buffer = lookupBuffer(channel);
			InputStream inputStream = new ByteBufferInputStream(buffer, index,
					size);
			JavaClass javaClass = new ClassParser(inputStream, fileName)
					.parse();
			return javaClass;
		} catch (IOException e) {
			throw new ArchiveCacheException(
					"IOException thrown while interacting with file channel", e);
		}
	}

	private ByteBuffer lookupBuffer(FileChannel channel) throws IOException {
		ResourceLocator locator = ResourceLocator.instance();
		String path = archiveFile.getAbsolutePath();
		if (!locator.contains(path)) {
			MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel
					.size());
			buffer.load();
			locator.register(path, buffer);
		}
		ByteBuffer buffer = (ByteBuffer) locator.lookup(path);
		return buffer;
	}

	private Properties loadIdxPropertiesFromUrl(URL url) {
		File archiveIdx = null;
		try {
			URLHelper helper = new URLHelper();
			archiveIdx = helper.toArchiveIdx(url);
			Properties idxProperties = new Properties();
			idxProperties.load(new FileInputStream(archiveIdx));
			return idxProperties;
		} catch (MalformedURLException e) {
			throw new ArchiveCacheException("URL was malformed : " + url, e);
		} catch (IOException e) {
			throw new ArchiveCacheException(
					"IOException thrown while loading properties : "
							+ archiveIdx, e);
		}
	}

}
