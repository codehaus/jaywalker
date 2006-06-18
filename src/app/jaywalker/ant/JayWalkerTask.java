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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

public class JayWalkerTask extends Task {

	protected Vector classlists = new Vector();

	private Set optionSet = new HashSet();

	private File outDir;

	private CommandlineJava commandline;

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
			createClasslistArgument(classlist);
			createOptionArguments();

			executeAsForked();

		} catch (Exception e) {
			throw new BuildException(e);
		}

	}

	private void createOptionArguments() {
		Option[] options = (Option[]) optionSet.toArray(new Option[optionSet
				.size()]);
		Properties properties = Option.toProperties(options);
		String[] arguments = toArguments(properties);
		for (int i = 0; i < arguments.length; i++) {
			getCommandline().createArgument().setValue(arguments[i]);
		}
	}

	protected File toTopLevelJarUrl(Class clazz) {
		String resourceName = asResourceName(clazz.getName());
		URL url = JayWalkerTask.class.getResource(resourceName);
		URL jarUrl = toTopLevelJarUrl(url);
		try {
			return new File(new URI(jarUrl.toString()));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
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
		getCommandline().createArgument().setValue(
				"-tempDir=" + tempDir.getAbsolutePath());
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
		getCommandline().createArgument().setValue(
				"-outDir=" + outDir.getAbsolutePath());
	}

	public CommandlineJava getCommandline() {
		if (commandline == null) {
			commandline = new CommandlineJava();
		}
		return commandline;
	}

	public void executeAsForked() {

		String jarFilePath = toTopLevelJarUrl(JayWalkerTask.class)
				.getAbsolutePath();
		commandline.setJar(jarFilePath);

		Path classPath = commandline.getClasspath();
		if (classPath != null) {
			commandline.createArgument().setValue("-classpath=" + classPath);
		}

		Execute execute = new Execute();
		execute.setCommandline(commandline.getCommandline());

		log(commandline.describeCommand(), Project.MSG_VERBOSE);

		try {
			execute.execute();
		} catch (IOException e) {
			throw new BuildException("Process fork failed.", e, getLocation());
		}
	}

	public void createClasslistArgument(String classlist) {
		getCommandline().createArgument().setValue("-classlist=" + classlist);
	}

	public String[] toArguments(Properties properties) {
		Set keySet = properties.keySet();
		String[] keys = (String[]) keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keys);
		String[] arguments = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			arguments[i] = "-" + keys[i] + "="
					+ properties.getProperty(keys[i]);
		}
		return arguments;
	}

	public URL toTopLevelJarUrl(URL url) {
		String urlString = url.toString();
		if (urlString.indexOf("jar:") == -1) {
			return url;
		}
		int startIdx = "jar:".length();
		int endIdx = urlString.indexOf("!/");
		try {
			return new URL(urlString.substring(startIdx, endIdx));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public void setClasspath(Path s) {
		createClasspath().append(s);
	}

	public void setClasspathRef(Reference r) {
		createClasspath().setRefid(r);
	}

	public Path createClasspath() {
		return getCommandline().createClasspath(getProject()).createPath();
	}

}
