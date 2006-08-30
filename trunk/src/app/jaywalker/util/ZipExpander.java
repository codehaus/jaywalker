package jaywalker.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jaywalker.util.ZipInputStreamVisitor.ZipEntryListener;

public class ZipExpander {

	private final ZipInputStream zis;

	public ZipExpander(ZipInputStream zis) {
		this.zis = zis;
	}

	public void expand(File dest) throws ZipExpanderException {
		validateDestination(dest);

		try {
			ZipInputStreamVisitor visitor = new ZipInputStreamVisitor(zis);
			visitor.addListener(createZipEntryListener(dest));
			visitor.accept();
		} catch (IOException e) {
			throw new ZipExpanderException(
					"IOException thrown while visiting ZipFile.", e);
		}

	}

	protected ZipEntryListener createZipEntryListener(final File dest) {
		return new ZipEntryListener() {
			public void visit(ZipInputStream zis, ZipEntry zipEntry)
					throws IOException {
				String filename = zipEntry.getName();

				File file = new File(dest, filename);
				if (zipEntry.isDirectory()) {
					file.mkdirs();
				} else {
					OutputStream os = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					int len;
					while ((len = zis.read(buf)) > 0) {
						os.write(buf, 0, len);
					}
					os.close();
				}
			}

		};
	}

	private void validateDestination(File dest) throws ZipExpanderException {
		if (dest == null) {
			throw new ZipExpanderException("Destination must be specified.");
		}

		if (dest.exists() && !dest.isDirectory()) {
			throw new ZipExpanderException("Destination must be a directory.");
		}
	}

}
