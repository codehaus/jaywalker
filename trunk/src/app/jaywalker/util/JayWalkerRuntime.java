package jaywalker.util;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JayWalkerRuntime {

	private final File file;

	public JayWalkerRuntime(File file) {
		this.file = file;
	}

	public String getVersion() {
		return getValue("Implementation-Version", "DEV");
	}

	private String getValue(String key, String defaultValue) {
		try {
			JarFile jarFile = new JarFile(file);
			Manifest manifest = jarFile.getManifest();
			String value = manifest.getMainAttributes().getValue(key);
			if (value == null) {
				return defaultValue;
			}
			return value;
		} catch (IOException e) {
			return defaultValue;
		}

	}

	public String getTitle() {
		return getValue("Implementation-Title", "UNKNOWN");
	}

}
