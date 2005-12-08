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

import jaywalker.ant.Option;

public class DependencyReportConfiguration {
    private final DependencyModel dependencyModel;

    public DependencyReportConfiguration(DependencyModel dependencyModel) {
        this.dependencyModel = dependencyModel;
    }

    public ReportTag [] toReportTags(Option [] options) {
        ReportTagMap dependencyReportTagMap = new ReportTagMap();
        dependencyReportTagMap.put("dependency", "archive", new ContainerDependencyReportTag(dependencyModel));
        dependencyReportTagMap.put("dependency", "class", new UnresolvedClassNameDependencyReportTag(dependencyModel));
        dependencyReportTagMap.put("cycle", "archive", new ContainerCyclicDependencyReportTag(dependencyModel));
        dependencyReportTagMap.put("cycle", "package", new PackageCyclicDependencyReportTag(dependencyModel));
        dependencyReportTagMap.put("cycle", "class", new ElementCyclicDependencyReportTag(dependencyModel));

        return dependencyReportTagMap.optionsToReportTags(options);
    }

}
