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

import jaywalker.util.HashCode;
import jaywalker.util.ResourceLocator;
import jaywalker.util.StringHelper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class URLHelper {
    private final static StringHelper stringHelper = new StringHelper();

    private final static File FILE_DOES_NOT_EXIST =
            new File(
                    "non-existant file used by jaywalker to avoid having to do a null check - " +
                            "if you create this on the file system yourself, " +
                            "you are a very silly, silly person.  " +
                            "Perhaps I'm equally as silly for doing this.");

    /**
     * Returns the URL encoding of the classlist element in the folowing format:
     * <p>[jar:]file:///path/[path/file-jar!/...]path/element</p>
     *
     * @return the URL encoding
     * @throws java.net.MalformedURLException
     * @throws java.net.URISyntaxException
     */
    public URL toEncodedURL(URL url) throws MalformedURLException, URISyntaxException {
        // Return fast if the element is not in an archive
        String str = url.toString();
        final int idx = str.lastIndexOf("!/");
        if (idx == -1) return url;

        // Construct URL with safe naming taken into account
        StringBuffer sb = new StringBuffer();
        sb.append("file://");
        if ( System.getProperty("os.name").toLowerCase().indexOf("windows") != -1 ) {
            sb.append("/");
        }
        sb.append(getTempDir().getAbsolutePath().replace('\\', '/'));
        sb.append("/");
        final String archiveDir = str.substring("jar:".length(), idx);
        sb.append(toSafeDirectoryName(new File(new URI(archiveDir)).getAbsolutePath()));
        if (!str.endsWith("!/")) {
            sb.append("/");
            sb.append(HashCode.encode(str.substring(idx + 2)));
        }
        return new URL(sb.toString());
    }

    private File getTempDir() {
        return (File) ResourceLocator.instance().lookup("tempDir");
    }

    protected String toSafeDirectoryName(String name) {
        return name.replace('.', '_').replace(':', '~').replace(File.separatorChar, '.');
    }

    public URL toParentURL(URL url) {
        String urlString = url.toString();
        try {
            urlString = urlString.substring(0, toLastContainerIdx(urlString));
            urlString = stripProtocolIfTopLevelArchive(urlString);
            return new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String stripProtocolIfTopLevelArchive(String directoryName) {
        int lastIdx = directoryName.lastIndexOf("!/");
        if (lastIdx != directoryName.length() - "!/".length()) return directoryName;
        int nextToLastIdx = directoryName.lastIndexOf("!/", lastIdx - 1);
        if (lastIdx != -1 && nextToLastIdx == -1) {
            return directoryName.substring("jar:".length(), lastIdx);
        } else {
            return directoryName.substring(0, lastIdx);
        }
    }


    public int toLastContainerIdx(String urlString) {
        int endIdx = urlString.lastIndexOf("/");
        if (urlString.endsWith("/")) {
            endIdx = urlString.lastIndexOf("/", endIdx - 1);
        }
        return endIdx + 1;
    }

    public File toParentFile(URL url) {
        return toEncodedFile(toParentURL(url));
    }

    public File toEncodedFile(URL url) {
        try {
            return new File(new URI(toEncodedURL(url).toString()));
        } catch (URISyntaxException e) {
            return FILE_DOES_NOT_EXIST;
        } catch (MalformedURLException e) {
            return FILE_DOES_NOT_EXIST;
        }
    }

    public URL[] toURLs(URL url, File[] files) {
        if (url == null || files == null) return new URL[0];
        String [] filenames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            filenames[i] = files[i].getName();
        }
        return toURLs(url, filenames);
    }

    public URL[] toURLs(URL url, String[] filenames) {
        if (url == null || filenames == null) return new URL[0];
        String [] urlStrings = prependUrlString(url, filenames);
        URL [] urls = new URL [urlStrings.length];
        try {
            for (int i = 0; i < urlStrings.length; i++) {
                urls[i] = new URL(urlStrings[i]);
            }
            return urls;
        } catch (MalformedURLException e) {
            return new URL[0];
        }
    }

//    URL [] urls = new URL [ filenames.length ];
//    String baseUrlString = url.toString();
//    baseUrlString = StringHelper.appendIfMissing("/", baseUrlString);
//    try {
//        for (int i = 0; i < filenames.length; i++) {
//            urls[i] = new URL(baseUrlString + filenames[i]);
//        }
//        return urls;
//    } catch (MalformedURLException e) {
//        return new URL[0];
//    }


    public URL appendIfMissing(String suffix, URL target) {
        String urlString = target.toString();
        urlString = stringHelper.appendIfMissing("/", urlString);
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public File toArchiveFile(URL url) {
        return toEncodedFile(url);
    }

    public File toArchiveDir(URL url) throws MalformedURLException {
        URL newURL = toArchiveURL(url);
        return toEncodedFile(newURL);
    }

    public URL toArchiveURL(URL url) throws MalformedURLException {
        String urlString = stringHelper.prependIfMissing("jar:", url.toString());
        return new URL(urlString + "!/");
    }

    public File toArchiveIdx(URL baseUrl) throws MalformedURLException {
        return new File(toArchiveDir(baseUrl), toArchiveFile(baseUrl).getName() + ".idx");
    }

    public File toArchiveLock(File archiveDir) {
        return new File(archiveDir.getParentFile(), archiveDir.getName() + ".lck");
    }

    private final static String [] LEGAL_EXTS = {".jar", ".ear", ".war", ".zip"};

    public boolean isLegalArchiveExtension(URL url) {
        String urlString = url.toString();
        for (int i = 0; i < LEGAL_EXTS.length; i++) {
            if (urlString.endsWith(LEGAL_EXTS[i])) return true;
        }
        return false;
    }

    public File toArchiveCls(URL baseUrl) throws MalformedURLException {
        return new File(toArchiveDir(baseUrl), toArchiveFile(baseUrl).getName() + ".cls");
    }

    public String[] prependUrlString(URL baseUrl, String[] filenames) {
        String urlString = baseUrl.toString();
        if (new URLHelper().isLegalArchiveExtension(baseUrl)) {
            urlString = stringHelper.prependIfMissing("jar:", urlString);
            urlString += "!/";
        } else {
            urlString += "/";
        }
        String [] urlStrings = new String[filenames.length];
        for (int i = 0; i < filenames.length; i++) {
            urlStrings[i] = urlString + filenames[i];
        }
        return urlStrings;
    }

    public String toBaseContainer(URL url) {
        if (url == null) return null;
        String urlString = url.toString();
        int idx;
        if ((idx = urlString.lastIndexOf("!/")) != -1) {
            return urlString.substring(0, idx);
        } else {
            return urlString.substring(0, urlString.lastIndexOf("/"));
        }
    }

}
