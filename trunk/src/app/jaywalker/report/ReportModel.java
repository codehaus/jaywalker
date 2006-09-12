package jaywalker.report;

import java.util.Properties;

import jaywalker.classlist.ClasslistElementListener;

public interface ReportModel {
	public Tag[] toReportTags(Properties properties);

	public String[] getReportTypes();

	public ClasslistElementListener getClasslistElementListener();
}
