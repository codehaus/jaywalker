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
package jaywalker.testutil;

import jaywalker.classlist.ClasslistElementFactory;
import jaywalker.classlist.ClasslistElementVisitor;

import java.io.File;
import java.io.InputStream;
import java.util.StringTokenizer;

public class Path {
	public final static File DIR_TEMP = new File("build" + File.separator
			+ "temp");

	public final static File DIR_BUILD_APP = new File("build" + File.separator
			+ "classes");

	public final static File DIR_BUILD_JAR = new File("build" + File.separator
			+ "data");

	public final static File DIR_TEST1 = new File("build" + File.separator
			+ "classes-test" + File.separator + "test1");

	public final static File DIR_TEST2 = new File("build" + File.separator
			+ "classes-test" + File.separator + "test2");

	public final static File FILE_TEST1_JAR = new File(DIR_BUILD_JAR,
			"jaywalker-test1.jar");

	public final static File FILE_TEST2_JAR = new File(DIR_BUILD_JAR,
			"jaywalker-test2.jar");

	public final static File FILE_TEST3_JAR = new File(DIR_BUILD_JAR,
			"jaywalker-test3.jar");

	public final static File FILE_TEST4_JAR = new File(DIR_BUILD_JAR,
			"jaywalker-test4.jar");

	public final static File FILE_TEST5_JAR = new File(DIR_BUILD_JAR,
			"jaywalker-test5.jar");

	public final static File FILE_CYCLE_PART1_JAR = new File(DIR_BUILD_JAR,
			"jaywalker-cycle-part1.jar");

	public final static File FILE_CYCLE_PART2_JAR = new File(DIR_BUILD_JAR,
			"jaywalker-cycle-part2.jar");

	public final static File FILE_TEST_EAR = new File(DIR_BUILD_JAR,
			"jaywalker-test.ear");

	public final static File FILE_CLASSLIST_ELEMENT_FACTORY_CLASS = new File(
			DIR_BUILD_APP, toFilename(ClasslistElementFactory.class));

	public final static File FILE_CLASSLIST_ELEMENT_VISITOR_CLASS = new File(
			DIR_BUILD_APP, toFilename(ClasslistElementVisitor.class));

	public static final File FILE_REPORT_ZIP = new File("build"
			+ File.separator + "dist" + File.separator + "report.zip");

	public static final File DIR_REPORT_ZIP = new File(DIR_TEMP, "report.zip");

	static {
		DIR_TEMP.mkdirs();
	}

	private static String toFilename(Class clazz) {
		if (clazz == null) {
			return "";
		}
		String classname = clazz.getName();
		if (clazz.getName().indexOf("class ") != -1) {
			classname = classname.substring("class ".length());
		}
		StringTokenizer st = new StringTokenizer(classname, ".");
		StringBuffer sb = new StringBuffer();
		while (st.hasMoreTokens()) {
			sb.append(st.nextToken());
			sb.append((st.hasMoreTokens()) ? File.separator : ".class");
		}
		return sb.toString();
	}
}
