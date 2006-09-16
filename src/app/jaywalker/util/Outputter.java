package jaywalker.util;

import java.io.InputStream;
import java.io.OutputStream;

public interface Outputter {

	public void transform(FileDecorator file, OutputStream outputStream);

	public void transform(InputStream is, OutputStream os);

}