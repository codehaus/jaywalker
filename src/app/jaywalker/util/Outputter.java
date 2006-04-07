package jaywalker.util;

import java.io.InputStream;
import java.io.OutputStream;

public interface Outputter {

	public void write(OutputStream outputStream);

	public void transform(InputStream is, OutputStream os);

}