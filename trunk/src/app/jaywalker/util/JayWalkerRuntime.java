package jaywalker.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JayWalkerRuntime {

	private final File file;

	public JayWalkerRuntime(File file) {
		this.file = file;
	}

	public JayWalkerRuntime() throws URISyntaxException {
		URL url = new URLHelper().toResource(JayWalkerRuntime.class);
		String urlString = url.toString();
		int idx = urlString.indexOf("!/");
		if (idx == -1) {
			file = null;
			return;
		}
		String jarName = new URLHelper()
				.stripProtocolIfTopLevelArchive(urlString.substring(0, idx));
		if (jarName.startsWith("jar:")) {
			jarName = jarName.substring("jar:".length());
		}

		file = new File(new URI(jarName));
	}

	public String getVersion() {
		return getValue("Implementation-Version", "DEV");
	}

	private String getValue(String key, String defaultValue) {
		if (file == null) {
			return defaultValue;
		}
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
