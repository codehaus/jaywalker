package jaywalker.util;

import java.io.File;


public class FileDecoratorFactory {

	public FileDecorator create(File outDir, String filename) {
		String value = System.getProperty("ReportFile");
		if (InMemoryFileDecorator.class.getName().equals(value)) {
			return new InMemoryFileDecorator(filename);
		}
		return new DefaultFileDecorator(new File(outDir, filename));
	}

}
