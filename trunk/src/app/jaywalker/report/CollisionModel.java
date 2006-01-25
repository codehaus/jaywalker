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

import jaywalker.classlist.ClassElement;
import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementEvent;
import jaywalker.classlist.ClasslistElementListener;

import java.util.Map;
import java.net.URL;

public class CollisionModel implements ClasslistElementListener {

    private CollisionHelper collisionHelper = new CollisionHelper();
    private SerialVersionUidHelper suidHelper;
    private Map urlCollisionMap;

    public void classlistElementVisited(ClasslistElementEvent event) {
        ClasslistElement classlistElement = event.getElement();
        if (classlistElement.getClass() == ClassElement.class) {
            ClassElement classElement = (ClassElement) classlistElement;
            collisionHelper.register(classElement.getURL(), classElement.getName());
        }
    }

    public void lastClasslistElementVisited() {
        urlCollisionMap = collisionHelper.createUrlCollisionMap();
        suidHelper = new SerialVersionUidHelper(collisionHelper.createClassNameToUrlsMap());
    }

    public URL [] lookupCollisionUrls(URL url) {
        return (URL[]) urlCollisionMap.get(url);        
    }

    public boolean isSerialVersionUidsConflicting(URL[] collisionUrls) {
        return suidHelper.isSerialVersionUidsConflicting(collisionUrls);
    }

    public long toSerialVersionUID(URL collisionUrl) {
        return suidHelper.toSerialVersionUID(collisionUrl);
    }

}
