package jaywalker.util;

public class ArchiveDependenciesResolvedHtmlTest extends
		HtmlDetailTableTestCase {

	public String getTitleValue() {
		return "Archive Resolved Dependencies";
	}

	public String[] getHeaderValues() {
		return new String[] { "Archive", "Dependency" };
	}

	public String getNoDataRowValue() {
		return "No Archives Found";
	}

	public String getXsltFileName() {
		return "archive-dependencies-resolved-html.xslt";
	}

}
