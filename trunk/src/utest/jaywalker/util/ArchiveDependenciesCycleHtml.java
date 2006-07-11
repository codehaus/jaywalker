package jaywalker.util;

public class ArchiveDependenciesCycleHtml extends HtmlDetailTableTestCase {

	public String getTitleValue() {
		return "Archive Cyclic Dependencies";
	}

	public String[] getHeaderValues() {
		return new String[] { "Archive", "Cycle" };
	}

	public String getNoDataRowValue() {
		return "No Cycles Found";
	}

	public String getXsltFileName() {
		return "archive-dependencies-cycle-html.xslt";
	}

}
