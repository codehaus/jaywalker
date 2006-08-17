package jaywalker.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipInputStreamVisitor {

	protected final ZipInputStream zis;

	protected final List listenerList = new ArrayList();

	public interface ZipEntryListener {
		public void visit(ZipInputStream zis, ZipEntry zipEntry)
				throws IOException;
	}

	public ZipInputStreamVisitor(ZipInputStream zis) {
		this.zis = zis;
	}

	public void accept() throws IOException {
		accept(this);
	}

	public void addListener(ZipEntryListener listener) {
		listenerList.add(listener);
	}

	protected void accept(ZipInputStreamVisitor visitor) throws IOException {
		ZipEntryListener[] listeners = (ZipEntryListener[]) listenerList
				.toArray(new ZipEntryListener[listenerList.size()]);
		ZipEntry nextEntry;
		while ((nextEntry = (ZipEntry) zis.getNextEntry()) != null) {
			visit(listeners, nextEntry);
			zis.closeEntry();
		}
	}

	protected void visit(ZipEntryListener[] listeners, ZipEntry nextEntry)
			throws IOException {
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].visit(zis, nextEntry);
		}
	}

}
