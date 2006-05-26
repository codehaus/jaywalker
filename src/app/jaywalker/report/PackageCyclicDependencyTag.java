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
package jaywalker.report;

import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.DirectoryContainer;
import jaywalker.classlist.ClasslistElementFactory;

import java.net.URL;
import java.util.Stack;

public class PackageCyclicDependencyTag implements Tag {
    private final DependencyModel model;
    private final ClasslistElementFactory factory = new ClasslistElementFactory();
    private final TagHelper reportHelper = new TagHelper();

    public PackageCyclicDependencyTag(DependencyModel model) {
        this.model = model;
    }

    public String create(URL url, Stack parentUrlStack) {
        URL [] packageCycleUrls = model.lookupPackageCycleDependency(url);
        if (packageCycleUrls == null) return "";
        String [] values = new String[packageCycleUrls.length];
        for (int i = 0; i < packageCycleUrls.length; i++) {
            ClasslistElement element = factory.create(packageCycleUrls[i]);
            if (element.getClass() == DirectoryContainer.class) {
                DirectoryContainer directory = (DirectoryContainer) element;
                values[i] = directory.getPackageName();
            } else {
                values[i] = "";
            }
        }
        return reportHelper.createDependencyTags("cycle", "container", "package", packageCycleUrls, values, parentUrlStack);
    }
}
