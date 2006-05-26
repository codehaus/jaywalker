package jaywalker.report;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import jaywalker.classlist.ArchiveContainer;
import jaywalker.classlist.ClassElement;
import jaywalker.classlist.ClasslistContainer;
import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.DirectoryContainer;
import jaywalker.classlist.FileElement;

public class ClasslistElementTagMediator implements ClasslistElementTag {
	private final static Map MAP_TAG = new HashMap();

	private final static ClasslistElementTag TAG_DEFAULT_ELEMENT = new DefaultClasslistElementTag();

	private final static ClasslistElementTag TAG_DEFAULT_CONTAINER = new DefaultClasslistContainerTag();

	static {
		MAP_TAG.put(ArchiveContainer.class, TAG_DEFAULT_CONTAINER);
		MAP_TAG.put(DirectoryContainer.class, new DirectoryContainerTag());
		MAP_TAG.put(ClassElement.class, new ClassElementTag());
		MAP_TAG.put(FileElement.class, TAG_DEFAULT_ELEMENT);
	}

	public String create(ClasslistElement element, Report[] reports,
			Stack parentUrlStack) {
		ClasslistElementTag tag = lookupTagBy(element);
		return tag.create(element, reports, parentUrlStack);
	}

	private ClasslistElementTag lookupTagBy(ClasslistElement element) {
		final Class clazz = element.getClass();
		if (MAP_TAG.containsKey(clazz)) {
			return (ClasslistElementTag) MAP_TAG.get(clazz);
		}
		return returnDefaultFor(element);

	}

	private ClasslistElementTag returnDefaultFor(ClasslistElement element) {
		if (element instanceof ClasslistContainer) {
			return TAG_DEFAULT_CONTAINER;
		}
		return TAG_DEFAULT_ELEMENT;
	}
}
