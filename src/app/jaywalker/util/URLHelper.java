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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class URLHelper {

	/**
	 * Returns the URL encoding of the classlist element in the folowing format:
	 * <p>
	 * [jar:]file:///path/[path/file-jar!/...]path/element
	 * </p>
	 * 
	 * @return the URL encoding
	 * @throws java.net.MalformedURLException
	 * @throws java.net.URISyntaxException
	 */
	public URL toEncodedURL(URL url) throws MalformedURLException,
			URISyntaxException {
		// Return fast if the element is not in an archive
		String str = url.toString();
		final int idx = str.lastIndexOf("!/");
		if (idx == -1)
			return url;

		// Construct URL with safe naming taken into account
		StringBuffer sb = new StringBuffer();
		sb.append("file://");
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			sb.append("/");
		}
		sb.append(getTempDir().getAbsolutePath().replace('\\', '/'));
		sb.append("/");
		final String archiveDir = str.substring("jar:".length(), idx);
		sb.append(toSafeDirectoryName(new File(new URI(archiveDir))
				.getAbsolutePath()));
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
		return name.replace('.', '_').replace(':', '~').replace(
				File.separatorChar, '.');
	}

	public URL toParentURL(URL url) {
		String urlString = url.toString();
		try {
			urlString = urlString.substring(0, toLastContainerIdx(urlString));
			urlString = stripProtocolIfTopLevelArchive(urlString);
			return new URL(urlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException("MalformedURLException thrown for \""
					+ urlString + "\" (" + url + ")", e);
		}
	}

	public String stripProtocolIfTopLevelArchive(String urlString) {
		int lastIdx = urlString.lastIndexOf("!/");
		if (lastIdx != urlString.length() - "!/".length())
			return urlString;
		int nextToLastIdx = urlString.lastIndexOf("!/", lastIdx - 1);
		if (lastIdx != -1 && nextToLastIdx == -1) {
			int idx = urlString.indexOf(":");
			return urlString.substring(idx + 1, lastIdx);
		} else {
			return urlString.substring(0, lastIdx);
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
		URL newUrl = null;
		try {
			newUrl = toEncodedURL(url);
			URI uri = new URI(newUrl.toString());
			return new File(uri);
		} catch (URISyntaxException e) {
			throw newIllegalArgumentException("Illegal URI Syntax", e, url,
					newUrl);
		} catch (MalformedURLException e) {
			throw newIllegalArgumentException("Malformed URL", e, url, newUrl);
		}
	}

	private IllegalArgumentException newIllegalArgumentException(
			String message, Exception e, URL url, URL newUrl) {
		return new IllegalArgumentException(message + " : " + e.getMessage()
				+ " " + toInfo(url, newUrl));
	}

	private String toInfo(URL url, URL newUrl) {
		StringBuffer sb = new StringBuffer("(");
		sb.append("url=").append(url);
		if (newUrl != null) {
			sb.append(",encodedUrl=").append(newUrl);
		}
		sb.append(")");
		return sb.toString();
	}

	public URL[] toURLs(URL url, File[] files) {
		if (url == null || files == null)
			return new URL[0];
		String[] filenames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			filenames[i] = files[i].getName();
		}
		return toURLs(url, filenames);
	}

	public URL[] toURLs(URL url, String[] filenames) {
		if (url == null || filenames == null)
			return new URL[0];
		String[] urlStrings = prependUrlString(url, filenames);
		URL[] urls = new URL[urlStrings.length];
		try {
			for (int i = 0; i < urlStrings.length; i++) {
				urls[i] = new URL(urlStrings[i]);
			}
			return urls;
		} catch (MalformedURLException e) {
			return new URL[0];
		}
	}

	public URL appendIfMissing(String suffix, URL target) {
		String urlString = target.toString();
		urlString = new StringDecorator(urlString).appendIfMissing("/");
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
		String urlString = new StringDecorator(url.toString())
				.prependIfMissing("jar:");
		return new URL(urlString + "!/");
	}

	public File toArchiveIdx(URL baseUrl) throws MalformedURLException {
		return new File(toArchiveDir(baseUrl), toArchiveFile(baseUrl).getName()
				+ ".idx");
	}

	public File toArchiveVersion(URL baseUrl) throws MalformedURLException {
		return new File(toArchiveDir(baseUrl), toArchiveFile(baseUrl).getName()
				+ ".ver");
	}

	public File toArchiveLock(File archiveDir) {
		return new File(archiveDir.getParentFile(), archiveDir.getName()
				+ ".lck");
	}

	private final static SortedArray LEGAL_EXT_SET = SortedArray
			.valueOf(new String[] { ".jar", ".ear", ".war", ".zip" });

	public boolean isLegalArchiveExtension(URL url) {
		String urlString = url.toString();
		return isLegalArchiveExtension(urlString);
	}

	public boolean isLegalArchiveExtension(String urlString) {
		int idx = urlString.lastIndexOf(".");
		if (idx == -1) {
			return false;
		}
		final String extension = urlString.substring(idx, urlString.length());
		return LEGAL_EXT_SET.contains(extension);
	}

	public URL toLegalArchiveUrl(URL url) throws MalformedURLException {
		String urlString = url.toString();
		return toLegalArchiveUrl(urlString);
	}

	private URL toLegalArchiveUrl(String urlString)
			throws MalformedURLException {
		if (isLegalArchiveExtension(urlString)) {
			return new URL(urlString);
		}
		String newUrlString = urlString;
		int idx = urlString.lastIndexOf("!/");
		if (idx != -1) {
			final String archiveUrlString = urlString.substring(0, idx + 2);
			newUrlString = stripProtocolIfTopLevelArchive(archiveUrlString);
		}
		if (isLegalArchiveExtension(newUrlString)) {
			return new URL(newUrlString);
		}
		return null;
	}

	public File toArchiveCls(URL baseUrl) throws MalformedURLException {
		return new File(toArchiveDir(baseUrl), toArchiveFile(baseUrl).getName()
				+ ".cls");
	}

	public File toArchiveCch(URL baseUrl) throws MalformedURLException {
		return new File(toArchiveDir(baseUrl), toArchiveFile(baseUrl).getName()
				+ ".cch");
	}

	public String[] prependUrlString(URL baseUrl, String[] filenames) {
		String urlString = baseUrl.toString();
		if (new URLHelper().isLegalArchiveExtension(baseUrl)) {
			urlString = new StringDecorator(urlString).prependIfMissing("jar:");
			urlString += "!/";
		} else {
			urlString += "/";
		}
		String[] urlStrings = new String[filenames.length];
		for (int i = 0; i < filenames.length; i++) {
			urlStrings[i] = urlString + filenames[i];
		}
		return urlStrings;
	}

	public String toBaseContainer(URL url) {
		if (url == null)
			return null;
		String urlString = url.toString();
		int idx;
		if ((idx = urlString.lastIndexOf("!/")) != -1) {
			urlString = urlString.substring(0, idx);
			if ((idx = urlString.lastIndexOf("!/")) != -1) {
				return urlString;
			} else {
				return urlString.substring("jar:".length());
			}
		} else {
			return urlString.substring(0, urlString.lastIndexOf("/"));
		}
	}

	public URL toBaseContainerUrl(URL url) {
		try {
			return new URL(toBaseContainer(url));
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error creating url", e);
		}
	}

	public String toFileName(URL url) {
		String urlString = url.toString();
		int idx = urlString.lastIndexOf('/');
		if (idx == -1)
			return "";
		return url.toString().substring(idx + 1);
	}

	public boolean isArchivedFile(URL url) {
		return url.getProtocol().startsWith("jar");
	}

	public URL toResource(Class resourceClass) {
		return URLHelper.class.getResource(asResourceName(resourceClass
				.getName()));
	}

	public String asResourceName(String resource) {
		if (!resource.startsWith("/")) {
			resource = "/" + resource;
		}
		resource = resource.replace('.', '/');
		resource = resource + ".class";
		return resource;
	}

}
