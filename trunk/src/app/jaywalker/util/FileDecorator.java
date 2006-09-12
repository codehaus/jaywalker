package jaywalker.util;

import java.io.Reader;
import java.io.Writer;

public interface FileDecorator {
	
	public Writer getWriter();
	
	public Reader getReader();

	public String getParentAbsolutePath();

}
