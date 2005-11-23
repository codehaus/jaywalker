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

import jaywalker.util.FileSystem;
import jaywalker.util.HashCode;
import jaywalker.util.URLHelper;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.DescendingVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ArchiveExpander {
    public void expand(URL url) throws IOException {
        URLHelper helper = new URLHelper();
        File archiveDir = helper.toArchiveDir(url);
        File archiveFile = helper.toArchiveFile(url);
        File archiveLock = helper.toArchiveLock(archiveDir);

        if (isCacheStale(archiveDir, archiveFile, archiveLock)) {
            FileSystem.delete(archiveDir);
        }
        if (! archiveDir.exists()) {
            writeToCache(url);
        }
    }

    private boolean isCacheStale(File archiveDir, File archiveFile, File archiveLock) {
        return archiveDir.exists() && (archiveDir.lastModified() <= archiveFile.lastModified() || archiveLock.exists());
    }

    private void writeToCache(URL url) throws IOException {
        URLHelper helper = new URLHelper();
        File archiveDir = helper.toArchiveDir(url);
        File archiveFile = helper.toArchiveFile(url);
        File archiveLock = helper.toArchiveLock(archiveDir);
        File archiveIdx = helper.toArchiveIdx(url);
        File archiveCls = helper.toArchiveCls(url);

        archiveLock.createNewFile();

        // Look at archive...
        ZipFile zip = new ZipFile(archiveFile);
        archiveDir.mkdirs();

        Properties idxProperties = new Properties();
        Properties clsProperties = new Properties();

        for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
            ZipEntry nextEntry = (ZipEntry) entries.nextElement();
            final String filename = nextEntry.getName();

            File subArchiveFile = new File(archiveDir, String.valueOf(HashCode.encode(filename)));

            if (nextEntry.isDirectory()) {
                subArchiveFile.createNewFile();
            } else {
                // Extract archives into archive dir
                subArchiveFile.getParentFile().mkdirs();
                writeInputStreamToFile(zip.getInputStream(nextEntry), nextEntry.getSize(), subArchiveFile);
                if (filename.endsWith(".class")) {
                    clsProperties.setProperty(filename, createClassFileString(subArchiveFile));
                }
            }

            idxProperties.setProperty(filename, subArchiveFile.getAbsolutePath());
        }
        idxProperties.store(new FileOutputStream(archiveIdx), archiveFile.getAbsolutePath());
        clsProperties.store(new FileOutputStream(archiveCls), archiveFile.getAbsolutePath());

        zip.close();

        archiveLock.delete();
    }

    private String createClassFileString(File subArchiveFile) throws IOException {
        JavaClass javaClass = new ClassParser(subArchiveFile.getAbsolutePath()).parse();
        StringBuffer sb = new StringBuffer();
        sb.append(javaClass.getClassName()).append("|");
        sb.append(javaClass.getSuperclassName()).append("|");
        final String[] interfaceNames = javaClass.getInterfaceNames();
        sb.append(interfaceNames.length).append("|");
        for (int i = 0; i < interfaceNames.length; i++) {
            sb.append(interfaceNames[i]).append("|");
        }
        // TODO: put in common area.
        Set set = new HashSet();
        DependencyVisitor dependencyVisitor = new DependencyVisitor();
        DescendingVisitor traverser = new DescendingVisitor(javaClass, dependencyVisitor);
        traverser.visit();
        Enumeration enumeration = dependencyVisitor.getDependencies();
        while (enumeration.hasMoreElements()) {
            String className = (String) enumeration.nextElement();
            if (!className.equals(javaClass.getClassName())) {
                set.add(className);
            }
        }
        dependencyVisitor.clearDependencies();
        String [] dependencies = (String[]) set.toArray(new String[set.size()]);
        sb.append(dependencies.length).append("|");
        for ( int i = 0; i < dependencies.length; i++ ) {
            sb.append(dependencies[i]).append("|");
        }
        return sb.toString();
    }

    private void writeInputStreamToFile(final InputStream inputStream, final long size, final File file) throws IOException {
        ReadableByteChannel input = Channels.newChannel(inputStream);
        FileChannel output = new FileOutputStream(file).getChannel();
        output.transferFrom(input, 0, size);
        input.close();
        output.close();
    }
}
