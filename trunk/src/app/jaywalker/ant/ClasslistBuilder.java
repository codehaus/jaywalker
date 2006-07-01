package jaywalker.ant;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.DirectoryScanner;

public class ClasslistBuilder {
	private Map map = new HashMap();

	public ClasslistBuilder() {
		map.put(Classlist.TYPE_DEFAULT, new StringBuffer());
		map.put(Classlist.TYPE_SHALLOW, new StringBuffer());
		map.put(Classlist.TYPE_SYSTEM, new StringBuffer());
	}

	public void append(Classlist classlist, DirectoryScanner ds) {
		StringBuffer sb = getStringBuffer(classlist);
		final String[] includedDirs = ds.getIncludedDirectories();
		final String[] includedFiles = ds.getIncludedFiles();
		String dirElements = includeElements(ds.getBasedir(), includedDirs);
		String fileElements = includeElements(ds.getBasedir(), includedFiles);
		if (dirElements.length() == 0 && fileElements.length() == 0) {
			return;
		}
		if (sb.length() > 0) {
			sb.append(File.pathSeparator);
		}
		sb.append(dirElements);
		if (dirElements.length() > 0 && fileElements.length() > 0) {
			sb.append(File.pathSeparator);
		}
		sb.append(fileElements);
	}

	private StringBuffer getStringBuffer(Classlist classlist) {
		String type = classlist.getType();
		if (map.containsKey(type)) {
			return (StringBuffer) map.get(type);
		}
		return (StringBuffer) map.get(Classlist.TYPE_DEFAULT);
	}

	public String getClasslist(String key) {
		StringBuffer sb = (StringBuffer) map.get(key);
		return sb.toString();
	}

	private String includeElements(File baseDir, String[] includedFiles) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < includedFiles.length; i++) {
			final File file = new File(baseDir, includedFiles[i]);
			sb.append(file.getAbsolutePath());
			sb.append(File.pathSeparator);
		}
		return sb.toString();
	}

	public void append(Classlist classlist, String nestedPath) {
		StringBuffer sb = getStringBuffer(classlist);
		if (sb.length() > 0) {
			sb.append(File.pathSeparator);
		}
		sb.append(nestedPath);
	}

}
