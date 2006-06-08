package jaywalker.report;

import java.io.Reader;
import java.io.Writer;

public interface ReportFile {
	
	public Writer getWriter();
	
	public Reader getReader();

	public String getParentAbsolutePath();

}
