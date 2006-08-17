package jaywalker.report;

import jaywalker.classlist.ArchiveContainer;
import jaywalker.classlist.ClasslistElement;

public class ArchiveContainerTag extends DefaultClasslistContainerTag {

	protected String toAttributeString(final ClasslistElement element) {
		final ArchiveContainer archiveContainer = (ArchiveContainer) element;
		return toValueAttributeWithPackageName(archiveContainer);
	}

	private String toValueAttributeWithPackageName(
			final ArchiveContainer archiveContainer) {
		final String packageName = archiveContainer.getPackageName();
		if (packageName != null) {
			return " value=\"" + packageName + "\"";
		}
		return "";
	}

}
