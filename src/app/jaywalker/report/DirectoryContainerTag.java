package jaywalker.report;

import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.DirectoryContainer;

public class DirectoryContainerTag extends DefaultClasslistContainerTag {

	protected String toAttributeString(final ClasslistElement element) {
		final DirectoryContainer directoryContainer = (DirectoryContainer) element;
		return toValueAttributeWithPackageName(directoryContainer);
	}

	private String toValueAttributeWithPackageName(
			final DirectoryContainer directoryContainer) {
		final String packageName = directoryContainer.getPackageName();
		if (packageName != null) {
			return " value=\"" + packageName + "\"";
		}
		return "";
	}

}
