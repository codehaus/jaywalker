/**
 * Copyright 2005 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jaywalker.report;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import jaywalker.util.ResourceLocator;
import jaywalker.util.Shell;

public class ResourceLocatorSetup {

	private static final ResourceLocator LOCATOR = ResourceLocator.instance();

	public void register(ReportFile reportFile) {
		LOCATOR.register("report.xml", reportFile);
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
	
	public void registerWorkingDir(String tempPath) throws IOException {
		File workingDir = Shell.toWorkingDir(tempPath);
		ResourceLocator.instance().register("tempDir", workingDir);
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

	public void registerDeepClasslist(String classlist) {
		ResourceLocator.instance().register("classlist-deep", classlist);
		ResourceLocator.instance().register("classlist-deep-value", classlist);
	}

	public void registerOutDir(File outDir) {
		ResourceLocator.instance().register("outDir", outDir);
	}

}
