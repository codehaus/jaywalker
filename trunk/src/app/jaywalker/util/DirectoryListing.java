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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class DirectoryListing {
    private static final StringHelper stringHelper = new StringHelper();
    private Map directoryMap = new HashMap();
    private final ResourceLocator locator = ResourceLocator.instance();

    public DirectoryListing(URL baseUrl, File fileIdx) throws IOException {
        directoryMap = createDirectoryMap(baseUrl, fileIdx);
    }

    public DirectoryListing(URL baseUrl) throws IOException {
        URLHelper helper = new URLHelper();
        String urlString = baseUrl.toString();

        int idx = urlString.lastIndexOf("!/");
        if (idx != -1 && !helper.isLegalArchiveExtension(baseUrl)) {
            URL adjustedUrl = new URL(helper.stripProtocolIfTopLevelArchive(urlString.substring(0, idx + 2)));
            directoryMap = lookupDirectoryMap(adjustedUrl);
        } else {
            if (helper.isLegalArchiveExtension(baseUrl)) {
                directoryMap = lookupDirectoryMap(baseUrl);
            } else {
                try {
                    File file = new File(new URI(urlString));
                    if (file.isDirectory()) {
                        File [] files = file.listFiles();
                        directoryMap = lookupDirectoryMap(baseUrl, files);
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private Map lookupDirectoryMap(URL url, File[] files) throws MalformedURLException {
        final String urlString = url.toString();
        final String key = "dl:" + urlString;
        Map map;
        if (! locator.contains(key)) {
            map = new HashMap();
            map.put(url, createUrlsFromDirectory(urlString, files));
            locator.register(key, map);
        } else {
            map = (Map) locator.lookup(key);
        }
        return map;
    }

    private Map lookupDirectoryMap(URL url) throws IOException {
        URLHelper helper = new URLHelper();
        final String key = "dl:" + url.toString();
        Map map;
        if (! locator.contains(key)) {
            map = createDirectoryMap(url, helper.toArchiveIdx(url));
            locator.register(key, map);
        } else {
            map = (Map) locator.lookup(key);
        }
        return map;
    }

    private URL[] createUrlsFromDirectory(String urlString, File [] files) throws MalformedURLException {
        URL [] urls = new URL[files.length];
        urlString = stringHelper.appendIfMissing("/", urlString);
        for (int i = 0; i < files.length; i++) {
            String newUrlString = urlString + files[i].getName();
            if (files[i].isDirectory()) {
                newUrlString = stringHelper.appendIfMissing("/", newUrlString);
            }
            urls[i] = new URL(newUrlString);
        }
        return urls;
    }

    private Map createDirectoryMap(URL baseUrl, File fileIdx) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(fileIdx));
        String [] filenames = new URLHelper().prependUrlString(baseUrl, toDecodedFilenames(properties));
        Map map = createStringMap(filenames);
        return convertStringMapToUrlMap(map);
    }

    private Map createStringMap(String[] filenames) {
        Map directoryMap = new HashMap();
        URLHelper helper = new URLHelper();

        for (int i = 0; i < filenames.length; i++) {
            int lastIdx = filenames[i].length() - 1;
            if (filenames[i].endsWith("/")) {
                directoryMap.put(filenames[i], new ArrayList());
                lastIdx -= 1;
            }
            int idx = filenames[i].lastIndexOf('/', lastIdx);
            String directoryName = filenames[i].substring(0, idx + 1);
            directoryName = helper.stripProtocolIfTopLevelArchive(directoryName);
            addToDirectoryList(filenames[i], directoryMap, directoryName);
        }
        return directoryMap;
    }

    private Map convertStringMapToUrlMap(Map directoryStringMap) throws MalformedURLException {
        Map directoryUrlMap = new HashMap();
        for (Iterator itKeys = directoryStringMap.keySet().iterator(); itKeys.hasNext();) {
            String keyString = (String) itKeys.next();
            URL keyUrl = new URL(keyString);
            List values = (List) directoryStringMap.get(keyString);
            URL[]urls = new URL[values.size()];
            int i = 0;
            for (Iterator itValues = values.iterator(); itValues.hasNext();) {
                String valueString = (String) itValues.next();
                urls[i++] = new URL(valueString);
            }
            directoryUrlMap.put(keyUrl, urls);
        }
        return directoryUrlMap;
    }

    private void addToDirectoryList(String filename, Map directoryMap, String directoryName) {
        List list = (List) directoryMap.get(directoryName);
        if (list == null) {
            list = new ArrayList();
            directoryMap.put(directoryName, list);
        }
        list.add(filename);
    }

    private String[] toDecodedFilenames(Properties properties) {
        String [] decodedFilenames = (String[]) properties.keySet().toArray(new String[properties.size()]);
        Arrays.sort(decodedFilenames);
        return decodedFilenames;
    }

    public URL[] toUrls(URL url) {
        URL [] urls = (URL[]) directoryMap.get(url);
        return (urls == null) ? new URL[0] : urls;
    }
}
