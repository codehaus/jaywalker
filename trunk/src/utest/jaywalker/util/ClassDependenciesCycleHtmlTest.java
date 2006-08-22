package jaywalker.util;

public class ClassDependenciesCycleHtmlTest extends HtmlDetailTableTestCase {

	public String getTitleValue() {
		return "Class Cyclic Dependencies";
	}

	public String[] getHeaderValues() {
		return new String[] { "Class", "Cycle" };
	}

	public String getNoDataRowValue() {
		return "No Cycles Found";
	}

	public String getXsltFileName() {
		return "class-dependencies-cycle-html.xslt";
	}
	
	public String getTableId() {
		return "class-dependencies-cycle-table";
	}


}
