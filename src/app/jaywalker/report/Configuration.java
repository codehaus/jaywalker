package jaywalker.report;

import java.util.Properties;

import jaywalker.classlist.ClasslistElementListener;
import jaywalker.util.XsltTransformer;
import jaywalker.xml.Tag;

public interface Configuration {
	public Tag[] toReportTags(Properties properties);

	public String[] getReportTypes();

	public XsltTransformer[] toXsltTransformers(Properties properties);

	public ClasslistElementListener getClasslistElementListener();
}
