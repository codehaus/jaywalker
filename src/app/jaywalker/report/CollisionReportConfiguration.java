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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import jaywalker.classlist.ClasslistElementListener;
import jaywalker.util.XsltTransformer;
import jaywalker.xml.CollisionTag;
import jaywalker.xml.NestedTag;
import jaywalker.xml.SerialVersionUidConflictTag;
import jaywalker.xml.Tag;

public class CollisionReportConfiguration implements Configuration {
	private final ReportTagMap collisionNestedReportTagMap;

	private final ReportTagMap collisionReportTagMap;

	private final CollisionModel collisionModel;

	public CollisionReportConfiguration(CollisionModel collisionModel) {
		this.collisionModel = collisionModel;
		this.collisionReportTagMap = createReportTagMap(collisionModel);
		this.collisionNestedReportTagMap = createNestedReportTagMap(collisionModel);
	}

	private ReportTagMap createNestedReportTagMap(CollisionModel collisionModel) {
		ReportTagMap collisionNestedReportTagMap = new ReportTagMap();
		collisionNestedReportTagMap.put("conflict", "class",
				new SerialVersionUidConflictTag(collisionModel),
				new XsltTransformer("class-conflicts-resolved-html.xslt"));
		return collisionNestedReportTagMap;
	}

	private ReportTagMap createReportTagMap(CollisionModel collisionModel) {
		ReportTagMap collisionReportTagMap = new ReportTagMap();
		collisionReportTagMap.put("collision", "class", new CollisionTag(
				collisionModel), new XsltTransformer(
				"class-collisions-resolved-html.xslt"));
		return collisionReportTagMap;
	}

	public Tag[] toReportTags(Properties properties) {
		NestedTag[] collisionNestedReportTags = toNestedReportTags(collisionNestedReportTagMap
				.getXmlTags(properties));
		CollisionTag collisionReportTag = (CollisionTag) collisionReportTagMap
				.getXmlTag("collision", "class");
		collisionReportTag.setNestedReportTags(collisionNestedReportTags);
		return collisionReportTagMap.getXmlTags(properties);
	}

	private NestedTag[] toNestedReportTags(Tag[] reportTags) {
		NestedTag[] nestedReportTags = new NestedTag[reportTags.length];
		for (int i = 0; i < reportTags.length; i++) {
			nestedReportTags[i] = (NestedTag) reportTags[i];
		}
		return nestedReportTags;
	}

	public String[] getReportTypes() {
		final List list = new ArrayList();
		list.addAll(Arrays.asList(collisionReportTagMap.getKeys()));
		list.addAll(Arrays.asList(collisionNestedReportTagMap.getKeys()));
		return (String[]) list.toArray(new String[list.size()]);
	}

	public XsltTransformer[] toXsltTransformers(Properties properties) {
		final List list = new ArrayList();
		list.addAll(Arrays.asList(collisionReportTagMap.getHtmlTransformers(properties)));
		list.addAll(Arrays.asList(collisionNestedReportTagMap.getHtmlTransformers(properties)));
		return (XsltTransformer[]) list.toArray(new XsltTransformer[list.size()]);
	}

	public ClasslistElementListener getClasslistElementListener() {
		return collisionModel;
	}

}
