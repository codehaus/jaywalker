package jaywalker.report;

import jaywalker.classlist.ClasslistContainer;
import jaywalker.classlist.ClasslistElement;

public class DirectoryContainerTag extends DefaultClasslistContainerTag {

	protected String toAttributeString(final ClasslistElement element) {
		final ClasslistContainer directoryContainer = (ClasslistContainer) element;
		return toValueAttributeWithPackageName(directoryContainer);
	}

	private String toValueAttributeWithPackageName(
			final ClasslistContainer directoryContainer) {
		final String packageName = directoryContainer.getPackageName();
		if (packageName != null) {
			return " value=\"" + packageName + "\"";
		}
		return "";
	}

}
