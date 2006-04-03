package jaywalker.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileVisitor {

	private final ZipFile zipFile;

	private final List listenerList = new ArrayList();

	public interface ZipEntryListener {
		public void visit(ZipFile zipFile, ZipEntry zipEntry)
				throws IOException;
	}

	public ZipFileVisitor(ZipFile zipFile) {
		this.zipFile = zipFile;
	}

	public void accept() throws IOException {
		accept(this);
	}

	public void addListener(ZipEntryListener listener) {
		listenerList.add(listener);
	}

	private void accept(ZipFileVisitor visitor) throws IOException {
		ZipEntryListener[] listeners = (ZipEntryListener[]) listenerList
				.toArray(new ZipEntryListener[listenerList.size()]);
		for (Enumeration entries = zipFile.entries(); entries.hasMoreElements();) {
			ZipEntry nextEntry = (ZipEntry) entries.nextElement();
			visit(listeners, nextEntry);
		}
	}

	private void visit(ZipEntryListener[] listeners, ZipEntry nextEntry)
			throws IOException {
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].visit(zipFile, nextEntry);
		}
	}

}
