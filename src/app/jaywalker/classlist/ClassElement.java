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

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import jaywalker.util.URLHelper;

public class ClassElement extends ClasslistElement {
    private ClassElementFile file;
    private JavaClass javaClass;

    public static class Creator implements ClasslistElementCreator {
        public boolean isType(URL url) {
            return url.toString().endsWith(".class");
        }

        public ClasslistElement create(URL url) {
            return new ClassElement(url);
        }
    }

    public ClassElement(URL url) {
        super(url);
    }

    private File getPropertiesFile() {
        String urlString = url.toString();
        return new File(urlString.substring(0, urlString.lastIndexOf('.')) + ".properties");
    }

    public String getName() {
        return getClassFile().getClassName();
    }

    public String getPackageName() {
        String name = getName();
        int idx = name.lastIndexOf('.');
        if ( idx != -1 ) return name.substring(0,idx);
        return "";
    }

    public String getSuperName() {
        return getClassFile().getSuperClassName();
    }

    public String[] getInterfaceNames() {
        return getClassFile().getInterfaceNames();
    }

    public String[] getDependencies() {
        return file.getDependencies();
    }


    private ClassElementFile getClassFile() {
        if (file == null) {
            file = new ClassElementFile(url);
        }
        return file;
    }

    public JavaClass getJavaClass() {
        try {
            if (javaClass == null) {
                URL encodedUrl = new URLHelper().toEncodedURL(url);
                javaClass = new ClassParser(encodedUrl.openStream(), encodedUrl.toString()).parse();
            }
            return javaClass;
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public String getType() {
        return "class";
    }

}
