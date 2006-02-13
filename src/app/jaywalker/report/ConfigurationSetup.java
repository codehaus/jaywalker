package jaywalker.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jaywalker.classlist.ClasslistElementListener;
import jaywalker.util.Outputter;
import jaywalker.xml.Tag;

public class ConfigurationSetup {

	private final Configuration[] configurations = new Configuration[] {
			new DependencyReportConfiguration(new DependencyModel()),
			new CollisionReportConfiguration(new CollisionModel()) };

	public String[] getReportTypes() {
		List reportTypeList = new ArrayList();
		for (int i = 0; i < configurations.length; i++) {
			reportTypeList.addAll(Arrays.asList(configurations[i]
					.getReportTypes()));
		}
		return (String[]) reportTypeList.toArray(new String[reportTypeList
				.size()]);
	}

	public Report[] toReports(Properties properties) {
		List reportList = new LinkedList();
		for (int i = 0; i < configurations.length; i++) {
			Tag[] reportTags = configurations[i].toReportTags(properties);
			if (reportTags.length > 0) {
				Outputter[] transformers = configurations[i]
						.toXsltTransformers(properties);
				reportList.add(new Report(reportTags, transformers));
			}
		}
		return (Report[]) reportList.toArray(new Report[reportList.size()]);
	}

	public ClasslistElementListener[] getClasslistElementListeners() {
		ClasslistElementListener[] listeners = new ClasslistElementListener[configurations.length];
		for (int i = 0; i < configurations.length; i++) {
			listeners[i] = configurations[i].getClasslistElementListener();
		}
		return listeners;
	}
	
	public String[] getReportDescriptions() {
		String[] reportTypes = getReportTypes();
		Map map = toReportTypeMap(reportTypes);
		Set keySet = map.keySet();
		List descriptionList = new ArrayList();
		for (Iterator itKey = keySet.iterator(); itKey.hasNext();) {
			final Object key = itKey.next();
			Set valueSet = (Set) map.get(key);
			StringBuffer sb = new StringBuffer();
			sb.append(key).append("=");
			for (Iterator itValue = valueSet.iterator(); itValue.hasNext();) {
				sb.append(itValue.next());
				if (itValue.hasNext())
					sb.append(",");
			}
			descriptionList.add(sb.toString());
		}
		return (String[]) descriptionList.toArray(new String[descriptionList
				.size()]);
	}

	private Map toReportTypeMap(String[] reportTypes) {
		Map map = new HashMap();
		for (int i = 0; i < reportTypes.length; i++) {
			String reportType = reportTypes[i];
			String[] reportTypeAttributes = reportType.split(",");
			Set set = (Set) map.get(reportTypeAttributes[0]);
			if (set == null) {
				set = new HashSet();
				map.put(reportTypeAttributes[0], set);
			}
			set.add(reportTypeAttributes[1]);
		}
		return map;
	}


}
