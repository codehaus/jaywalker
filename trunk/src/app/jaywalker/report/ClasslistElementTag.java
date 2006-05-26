package jaywalker.report;

import java.util.Stack;

import jaywalker.classlist.ClasslistElement;

public interface ClasslistElementTag {
	public String create(ClasslistElement element, Report[] reports, Stack parentUrlStack);
}
