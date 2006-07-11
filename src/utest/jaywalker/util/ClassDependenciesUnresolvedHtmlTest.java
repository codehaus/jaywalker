package jaywalker.util;

public class ClassDependenciesUnresolvedHtmlTest extends HtmlDetailTableTestCase {

	public String getTitleValue() {
		return "Class Unresolved Dependencies";
	}

	public String[] getHeaderValues() {
		return new String[] { "Unresolved Class Name", "Dependent" };
	}

	public String getNoDataRowValue() {
		return "No Unresolved Dependencies Found";
	}

	public String getXsltFileName() {
		return "class-dependencies-unresolved-html.xslt";
	}

}
