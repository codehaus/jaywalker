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
package jaywalker.classlist;

import jaywalker.util.DirectoryListing;
import jaywalker.util.URLHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DirectoryContainer extends ClasslistContainer {

    private final String packageName;

    public static class Creator implements ClasslistElementCreator {
        public boolean isType(URL url) {
            final File file = new URLHelper().toEncodedFile(url);
            return file.exists() && url.toString().endsWith("/");
        }

        public ClasslistElement create(URL url) {
            return new DirectoryContainer(new URLHelper().appendIfMissing("/", url));
        }
    }

    public DirectoryContainer(URL url) {
        super(url);
        urls = toUrls(url);
        packageName = toPackageName(urls);
    }

    private URL[] toUrls(URL url) {
        try {
            URLHelper helper = new URLHelper();
            final File file = helper.toEncodedFile(url);
            if (!file.exists()) return new URL[0];
            DirectoryListing listing = new DirectoryListing(url);
            return listing.toUrls(url);
        } catch (IOException e) {
            e.printStackTrace();
            return new URL[0];
        }
    }

    public String getType() {
        return "directory";
    }

    public String getPackageName() {
        return packageName;
    }

    private String toPackageName(URL [] urls) {
        for (int i = 0; i < urls.length; i++) {
            if (urls[i].toString().endsWith(".class")) {
                ClasslistElementFactory factory = new ClasslistElementFactory();
                ClassElement classElement = (ClassElement) factory.create(urls[i]);
                return classElement.getPackageName();
            }
        }
        return null;
    }

}
