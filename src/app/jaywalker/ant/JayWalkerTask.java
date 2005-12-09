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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import jaywalker.classlist.ClasslistElementCache;
import jaywalker.report.ReportExecutor;
import jaywalker.util.ResourceLocator;
import jaywalker.util.Shell;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class JayWalkerTask extends Task {

	private File tempDir;

	protected Vector classlists = new Vector();

	private File output;

	private Set optionSet = new HashSet();

	public JayWalkerTask() {
		super();
		optionSet.add(new Option("dependency", "archive,class"));
		optionSet.add(new Option("collision", "class"));
		optionSet.add(new Option("conflict", "class"));
	}

	protected void validate() {
		if (output == null) {
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
			getProject().setNewProperty("classlist", classlist);
			String tempPath = (tempDir != null) ? tempDir.getAbsolutePath()
					: "";
			ResourceLocator.instance().register("tempDir",
					Shell.toWorkingDir(tempPath));
			ResourceLocator.instance().register("classlistElementCache",
					new ClasslistElementCache());

			Option[] options = (Option[]) optionSet
					.toArray(new Option[optionSet.size()]);
			Properties properties = Option.toProperties(options);

			BufferedWriter out = new BufferedWriter(new FileWriter(output));
			new ReportExecutor().execute(classlist, properties, out);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

	}

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

	public void addClasslist(Classlist classlist) {
		classlists.add(classlist);
	}

	public void addOption(Option option) {
		optionSet.add(option);
	}

	private String createClasslist() {
		StringBuffer sb = new StringBuffer();
		for (Iterator itFilesets = classlists.iterator(); itFilesets.hasNext();) {
			FileSet fs = (FileSet) itFilesets.next();
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			String[] includedFiles = ds.getIncludedFiles();
			for (int i = 0; i < includedFiles.length; i++) {
				sb.append(new File(ds.getBasedir(), includedFiles[i])
						.getAbsolutePath());
				sb.append(File.pathSeparator);
			}
		}
		return sb.toString();
	}

	public void setOutput(File output) {
		this.output = output;
	}

}
