package jaywalker.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public interface FileDecorator {
	
	public Writer getWriter();
	
	public OutputStream getOutputStream();
	
	public Reader getReader();
	
	public InputStream getInputStream();

	public String getParentAbsolutePath();

}
