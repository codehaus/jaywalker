package jaywalker.util;

import java.io.OutputStream;

public interface Outputter {

	public void write(OutputStream outputStream);

	public String transform(String value);

}