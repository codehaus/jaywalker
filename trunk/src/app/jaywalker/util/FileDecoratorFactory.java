package jaywalker.util;

import java.io.File;


public class FileDecoratorFactory {

	public FileDecorator create(String filename) {
		String value = System.getProperty("ReportFile");
		if (InMemoryFileDecorator.class.getName().equals(value)) {
			return new InMemoryFileDecorator(filename);
		}
		File outDir = (File) ResourceLocator.instance().lookup("outDir");
		return new DefaultFileDecorator(new File(outDir, filename));
	}

}
