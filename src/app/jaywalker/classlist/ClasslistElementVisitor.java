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

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class ClasslistElementVisitor {
    private final ClasslistElement[] classlistElements;
    private List listeners = new ArrayList();

    public ClasslistElementVisitor(ClasslistElement[] classlistElements) {
        this.classlistElements = classlistElements;
    }

    public ClasslistElementVisitor(ClasslistContainer cl) {
        this.classlistElements = cl.getClasslistElements();
    }

    public void accept(ClasslistElementVisitor v) throws IOException {
        for (int i = 0; i < classlistElements.length; i++) {
            v.visit(classlistElements[i]);
        }
    }

    public void visit(ClasslistElement cle) throws IOException {

        notifyListeners(cle);

        if (cle instanceof ClasslistContainer) {
            new ClasslistElementVisitor((ClasslistContainer) cle).accept(this);
        }

    }

    private void notifyListeners(ClasslistElement element) {
        ClasslistElementEvent event = new ClasslistElementEvent(element);
        for (int i = 0; i < listeners.size(); i++) {
            ClasslistElementListener listener = (ClasslistElementListener) listeners.get(i);
            listener.classlistElementVisited(event);
        }
    }

    public void addListener(ClasslistElementListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

}
