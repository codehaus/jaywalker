package jaywalker.util;

public class PackageDependenciesResolvedHtmlTest extends
		HtmlDetailTableTestCase {

	public String getTitleValue() {
		return "Package Resolved Dependencies";
	}

	public String[] getHeaderValues() {
		return new String[] { "Package", "Dependency" };
	}

	public String getNoDataRowValue() {
		return "No Packages Found";
	}

	public String getXsltFileName() {
		return "package-dependencies-resolved-html.xslt";
	}

}
