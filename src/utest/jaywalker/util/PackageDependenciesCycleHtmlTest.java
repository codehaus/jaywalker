package jaywalker.util;

public class PackageDependenciesCycleHtmlTest extends HtmlDetailTableTestCase {

	public String getTitleValue() {
		return "Package Cyclic Dependencies";
	}

	public String[] getHeaderValues() {
		return new String[] { "Package", "Cycle" };
	}

	public String getNoDataRowValue() {
		return "No Cycles Found";
	}

	public String getXsltFileName() {
		return "package-dependencies-cycle-html.xslt";
	}
	
	public String getTableId() {
		return "package-dependencies-cycle-table";
	}


}
