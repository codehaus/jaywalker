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

import java.util.Properties;

public class CollisionReportConfiguration {
	private final ReportTagMap collisionNestedReportTagMap;
	private final ReportTagMap collisionReportTagMap;

	public CollisionReportConfiguration(CollisionModel collisionModel) {
		this.collisionReportTagMap = createReportTagMap(collisionModel);
		this.collisionNestedReportTagMap = createNestedReportTagMap(collisionModel);
	}

	private ReportTagMap createNestedReportTagMap(CollisionModel collisionModel) {
		ReportTagMap collisionNestedReportTagMap = new ReportTagMap();
		collisionNestedReportTagMap.put("conflict", "class",
				new SerialVersionUidConflictReportTag(collisionModel));
		return collisionNestedReportTagMap;
	}

	private ReportTagMap createReportTagMap(CollisionModel collisionModel) {
		ReportTagMap collisionReportTagMap = new ReportTagMap();
		collisionReportTagMap.put("collision", "class", new CollisionReportTag(
				collisionModel));
		return collisionReportTagMap;
	}
	
	public ReportTag[] toReportTags(Properties properties) {
		NestedReportTag[] collisionNestedReportTags = toNestedReportTags(collisionNestedReportTagMap
				.get(properties));
		CollisionReportTag collisionReportTag = (CollisionReportTag) collisionReportTagMap.get("collision","class");
		collisionReportTag.setNestedReportTags(collisionNestedReportTags);
		return collisionReportTagMap.get(properties);
	}

	private NestedReportTag[] toNestedReportTags(ReportTag[] reportTags) {
		NestedReportTag[] nestedReportTags = new NestedReportTag[reportTags.length];
		for (int i = 0; i < reportTags.length; i++) {
			nestedReportTags[i] = (NestedReportTag) reportTags[i];
		}
		return nestedReportTags;
	}

}
