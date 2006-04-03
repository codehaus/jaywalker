package jaywalker.classlist;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import jaywalker.util.FileSystem;
import jaywalker.util.HashCode;
import jaywalker.util.URLHelper;

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
		File file = new File(archiveDir, String.valueOf(HashCode
				.encode(filename)));
		file.getParentFile().mkdirs();
		if (file.exists())
			file.delete();
		file.createNewFile();
		return file;
	}

	public File toArchiveFile() {
		return archiveFile;
	}

	public URL getURL() {
		return url;
	}

}
