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
import jaywalker.util.CollectionHelper;

public class ReportModelSetup {

	private final static CollectionHelper HELPER_COLLECTION = new CollectionHelper();

	private final ReportModel[] models = new ReportModel[] {
			new DependencyReportModel(new DependencyModel()),
			new CollisionReportModel(new CollisionModel()) };

	public String[] getReportTypes() {
		List reportTypeList = new ArrayList();
		for (int i = 0; i < models.length; i++) {
			reportTypeList.addAll(Arrays.asList(models[i]
					.getReportTypes()));
		}
		return HELPER_COLLECTION.toStrings(reportTypeList);
	}

	public Report[] toReports(Properties properties) {
		List reportList = new LinkedList();
		for (int i = 0; i < models.length; i++) {
			Tag[] reportTags = models[i].toReportTags(properties);
			if (reportTags.length > 0) {
				reportList.add(new Report(reportTags));
			}
		}
		return (Report[]) reportList.toArray(new Report[reportList.size()]);
	}

	public ClasslistElementListener[] getClasslistElementListeners() {
		ClasslistElementListener[] listeners = new ClasslistElementListener[models.length];
		for (int i = 0; i < models.length; i++) {
			listeners[i] = models[i].getClasslistElementListener();
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
		return HELPER_COLLECTION.toStrings(descriptionList);
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
