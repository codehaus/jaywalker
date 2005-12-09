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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Properties;

import jaywalker.classlist.ClasslistElementCache;
import jaywalker.report.ReportExecutor;
import jaywalker.util.ResourceLocator;
import jaywalker.util.Shell;

public class JayWalker {

	private ReportExecutor executor = new ReportExecutor();

	protected Properties toProperties(String[] args) {
		final Properties properties = new Properties();
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null && args[i].charAt(0) == '-') {
				String[] keyValues = args[i].substring(1).split("=");
				if (keyValues.length == 2) {
					final String key = keyValues[0].trim();
					String value = properties.getProperty(key);
					if (value != null) {
						value += "," + keyValues[1].trim();
					} else {
						value = keyValues[1].trim();
					}
					properties.put(key, value);
				} else {
					printInvalidArgument(args[i]);
				}
			} else {
				printInvalidArgument(args[i]);
			}
		}
		return properties;
	}

	private void printInvalidArgument(String arg) {
		System.out.println("invalid argument: " + arg);
	}

	protected static void printUsage() {
		StringBuffer sb = new StringBuffer();
		final String className = JayWalker.class.getName();
		final int argColumn = 17 + className.length();
		sb.append("usage   :  java ").append(className).append(" ");
		sb.append("-classlist=");
		sb.append("element1").append(File.pathSeparator);
		sb.append("element2").append(File.pathSeparator).append("...\n");
		sb.append(toSpaces(argColumn));
		sb.append("[-reportType=reportAttribute ...]\n");
		sb.append(toSpaces(argColumn));
		sb.append("[-tempDir=path]\n");
		System.out.println(sb.toString());
	}

	private static String toSpaces(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

	public void execute(String[] args) throws IOException {
		Properties properties = toProperties(args);
		String classlist = removeRequired("classlist", properties);
		String tempDir = removeOptional("tempDir", properties);

		ResourceLocator.instance().register("tempDir",
				Shell.toWorkingDir(tempDir));
		ResourceLocator.instance().register("classlistElementCache",
				new ClasslistElementCache());

		final PrintWriter writer = new PrintWriter(System.out);
		executor.execute(classlist, properties, writer);
	}

	private String removeOptional(String key, Properties properties) {
		return (String) properties.remove(key);
	}

	private String removeRequired(String key, Properties properties) {
		String value = (String) properties.remove(key);
		if (value == null) {
			throw new IllegalArgumentException(key + " is missing");
		}
		return value;
	}

	private static void fail(String message) {
		System.out.println(message);
		printUsage();
		System.exit(-1);
	}

	public static void main(String[] args) {
		try {
			JayWalker jayWalker = new JayWalker();
			String[] newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
			jayWalker.execute(newArgs);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
