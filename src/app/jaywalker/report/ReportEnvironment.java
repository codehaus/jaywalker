package jaywalker.report;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import jaywalker.util.FileSystem;
import jaywalker.util.ResourceLocator;
import jaywalker.util.Shell;
import jaywalker.util.StringDecorator;

public class ReportEnvironment {

	private static final ResourceLocator LOCATOR = ResourceLocator.instance();

	public void initialize(String classlist, Properties properties,
			File outDir, String tempPath) throws IOException {
		registerWorkingDir(tempPath);
		registerDeepClasslist(classlist);
		register(properties);
		initializeDefaults(properties);
		printClasslist(classlist);
		initOutDir(outDir);
	}

	private void registerWorkingDir(String tempPath) throws IOException {
		File workingDir = Shell.toWorkingDir(tempPath);
		LOCATOR.register("tempDir", workingDir);
	}

	private void initializeDefaults(Properties properties) {
		setDefaultProperty(properties, "dependency", "archive,package,class");
		setDefaultProperty(properties, "collision", "class");
		setDefaultProperty(properties, "conflict", "class");
	}

	private void setDefaultProperty(Properties properties, String reportType,
			String defaultValue) {
		if (properties.getProperty(reportType) == null) {
			properties.setProperty(reportType, defaultValue);
		}
	}

	private void printClasslist(final String classlist) {
		System.out.print("Walking the classlist:\n");
		System.out.println(new StringDecorator(classlist).spaceAndReplace(2,
				File.pathSeparator, "\n"));
		System.out.println();
	}

	private void initOutDir(File outDir) {
		if (outDir.exists()) {
			FileSystem.delete(outDir);
		}
		outDir.mkdir();
	}

	private void registerDeepClasslist(String classlist) {
		LOCATOR.register("classlist-deep", classlist);
		LOCATOR.register("classlist-deep-value", classlist);
	}

	public void register(Properties properties) {
		registerClasslist(properties, "classlist-shallow");
		LOCATOR.register("classlist-shallow-value",
				lookupClasslist("classlist-shallow"));
		registerClasslist(properties, "classlist-system");
		LOCATOR.register("classlist-system-value",
				lookupClasslist("classlist-system"));
		registerIncludeJayWalkerJarFile(properties);
	}

	private void registerClasslist(Properties properties, String classlistType) {
		if (properties.containsKey(classlistType)) {
			String classpath = properties.getProperty(classlistType);
			String[] classpaths = classpath.split(File.pathSeparator);
			File[] files = new File[classpaths.length];
			for (int i = 0; i < classpaths.length; i++) {
				files[i] = new File(classpaths[i]);
			}
			ResourceLocator.instance().register(classlistType, files);
		}
	}

	private void registerIncludeJayWalkerJarFile(Properties properties) {
		if (properties.containsKey("includeJayWalkerJarFile")) {
			Boolean includeJayWalkerJarFile = Boolean.valueOf(properties
					.getProperty("includeJayWalkerJarFile"));
			ResourceLocator.instance().register("includeJayWalkerJarFile",
					includeJayWalkerJarFile);
		}
	}

	private String lookupClasslist(String classlistType) {
		StringBuffer sb = new StringBuffer();
		if (ResourceLocator.instance().contains(classlistType)) {
			File[] files = (File[]) ResourceLocator.instance().lookup(
					classlistType);
			for (int i = 0; i < files.length; i++) {
				sb.append(files[i].getAbsoluteFile());
				if (i + 1 < files.length) {
					sb.append(File.pathSeparator);
				}
			}
		}
		return sb.toString();
	}

}
