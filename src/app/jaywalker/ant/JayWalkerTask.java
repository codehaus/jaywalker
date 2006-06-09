/**
 * Copyright 2005 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jaywalker.ant;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import com.simontuffs.onejar.JarClassLoader;

public class JayWalkerTask extends Task {

	private File tempDir;

	protected Vector classlists = new Vector();

	private Set optionSet = new HashSet();

	private File outDir;

	public JayWalkerTask() {
		super();
	}

	protected void validate() {
		if (outDir == null) {
			throw new BuildException("report output not set");
		}
		if (classlists.size() < 1) {
			throw new BuildException("classlist not set");
		}
	}

	public void execute() {
		validate();

		try {
			String classlist = createClasslist();
			registerClasslist(classlist);
			String tempPath = (tempDir != null) ? tempDir.getAbsolutePath()
					: "";

			Option[] options = (Option[]) optionSet
					.toArray(new Option[optionSet.size()]);
			Properties properties = Option.toProperties(options);

			executeReport(classlist, tempPath, properties);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		} finally {
		}

	}

	private void executeReport(String classlist, String tempPath,
			Properties properties) throws ClassNotFoundException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			MalformedURLException {

		JarClassLoader loader = new JarClassLoader(JayWalkerTask.class
				.getClassLoader());
		Thread.currentThread().setContextClassLoader(loader);

		File file = toClasspathRootFile(JayWalkerTask.class);
		String absolutePath = file.getAbsolutePath();
		String className = loader.load("jaywalker.report.ReportExecutor",
				absolutePath);
		Class clazz = loader.loadClass(className);
		Object reportExecutor = clazz.newInstance();
		Method execute = clazz.getMethod("execute", new Class[] { String.class,
				Properties.class, File.class, String.class });
		execute.invoke(reportExecutor, new Object[] { classlist, properties,
				outDir, tempPath });

	}
	
	protected File toClasspathRootFile(Class clazz) {
		String resourceName = asResourceName(clazz.getName());
		URL url = JayWalkerTask.class.getResource(resourceName);
		String urlString = url.toString();
		String uriString = urlString.substring(0, urlString.length()
				- resourceName.length() + 1);
		uriString = stripProtocolIfTopLevelArchive(uriString);
		try {
			return new File(new URI(uriString));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected String stripProtocolIfTopLevelArchive(String urlString) {
		int lastIdx = urlString.lastIndexOf("!/");
		if (lastIdx != urlString.length() - "!/".length())
			return urlString;
		int nextToLastIdx = urlString.lastIndexOf("!/", lastIdx - 1);
		if (lastIdx != -1 && nextToLastIdx == -1) {
			int idx = urlString.indexOf(":");
			return urlString.substring(idx + 1, lastIdx);
		} else {
			return urlString.substring(0, lastIdx);
		}
	}

	protected String asResourceName(String resource) {
		if (!resource.startsWith("/")) {
			resource = "/" + resource;
		}
		resource = resource.replace('.', '/');
		resource = resource + ".class";
		return resource;
	}

	private void registerClasslist(String classlist) {
		getProject().setNewProperty("classlist", classlist);
	}

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

	public void addClasslist(Classlist classlist) {
		classlists.add(classlist);
	}

	public void addConfiguredOption(Option option) {
		optionSet.add(option);
	}

	protected String createClasslist() {
		StringBuffer sb = new StringBuffer();
		for (Iterator itFilesets = classlists.iterator(); itFilesets.hasNext();) {
			FileSet fs = (FileSet) itFilesets.next();
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			final String[] includedDirs = ds.getIncludedDirectories();
			final String[] includedFiles = ds.getIncludedFiles();
			sb.append(includeElements(ds.getBasedir(), includedDirs));
			sb.append(includeElements(ds.getBasedir(), includedFiles));
		}
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

	public void setOutDir(File outDir) {
		this.outDir = outDir;
	}

}
