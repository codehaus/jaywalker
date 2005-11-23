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
package jaywalker.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class Shell {
    private static final StringHelper stringHelper = new StringHelper();
    private static final Runtime runtime = Runtime.getRuntime();

    public static String getEnvironment(String variable) throws IOException {
        String value = getEnvironment().getProperty(variable);
        if (value == null) {
            throw new IllegalArgumentException("Environment variable \"" + variable + "\" does not exist.");
        }
        return value;
    }

    public static Properties getEnvironment() throws IOException {
        Process p = null;
        Properties envVars = new Properties();
        Runtime r = Runtime.getRuntime();
        String OS = System.getProperty("os.name").toLowerCase();
        // System.out.println(OS);
        if (OS.indexOf("windows 9") > -1) {
            p = r.exec("command.com /c set");
        } else if ((OS.indexOf("nt") > -1)
            || (OS.indexOf("windows 2000") > -1)
            || (OS.indexOf("windows xp") > -1)) {
            p = r.exec("cmd.exe /c set");
        } else {
            p = r.exec("env");
        }
        BufferedReader br = new BufferedReader
            (new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            envVars.setProperty(stringHelper.substringBeforeLast(line, "="), stringHelper.substringAfterLast(line, "="));
        }
        return envVars;
    }

    public static File toWorkingDir(String tempPath) throws IOException {
        File tempDir = new File((stringHelper.isBlank(tempPath)) ? getEnvironment("TEMP") : tempPath);
        File workingDir = new File(tempDir, "jaywalker");
        workingDir.mkdir();
        return workingDir;
    }

    public static File toWorkingDir() throws IOException {
        return toWorkingDir("");
    }

    public static long usedMemory() {
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public static void runGC() {
        runtime.runFinalization();
        runtime.gc();
        Thread.currentThread().yield();
    }

}
