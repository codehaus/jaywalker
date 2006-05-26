package jaywalker.report;

import java.util.Properties;

import jaywalker.classlist.ClasslistElementListener;
import jaywalker.util.Outputter;

public interface Configuration {
	public Tag[] toReportTags(Properties properties);

	public String[] getReportTypes();

	public Outputter[] toXsltTransformers(Properties properties);

	public ClasslistElementListener getClasslistElementListener();
}
