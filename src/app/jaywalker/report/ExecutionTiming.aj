package jaywalker.report;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import jaywalker.classlist.ClasslistElement;
import jaywalker.classlist.ClasslistElementStatistic;
import jaywalker.classlist.ClasslistElementVisitor;
import jaywalker.util.Clock;
import jaywalker.util.FileDecorator;
import jaywalker.util.ResourceLocator;

public aspect ExecutionTiming {

	pointcut initialize(ClasslistElementVisitor visitor) : call(void initialize(ClasslistElementVisitor) throws IOException) && args(visitor);

	pointcut outputXml(FileDecorator reportFile, Report[] reports,
			ClasslistElement[] classlistElements) : call(void outputXml(FileDecorator, Report[], ClasslistElement[]) 
			throws IOException) && args(reportFile, reports, classlistElements);

	private static final String MSG_JAYWALKER_REPORT_TOTAL = "Total time to create the JayWalker report";

	private static final String MSG_XML_REPORT_TOTAL = "    Total time to create XML report";

	private static final String MSG_INIT_REPORT_TOTAL = "      Time to initialize the report";

	private static final String MSG_HTML_REPORT_TOTAL = "    Time to create HTML report";

	private static final String MSG_AGGREGATE_TOTAL = "      Time to aggregate report elements";

	private final Clock clock = getClockResource();

	void around(): call(void execute(String, Properties, File,
			String) throws IOException) {
		System.out.println("Creating the JayWalker report . . .");
		clock.start(MSG_JAYWALKER_REPORT_TOTAL);
		proceed();
		stopClock(MSG_JAYWALKER_REPORT_TOTAL);
	}

	void around(): call(void outputXml(File, String, Report[], String) throws IOException) {
		System.out.println("  Creating XML report . . .");
		clock.start(MSG_XML_REPORT_TOTAL);
		proceed();
		stopClock(MSG_XML_REPORT_TOTAL);
	}

	void around(FileDecorator reportFile, Report[] reports,
			ClasslistElement[] classlistElements): outputXml(reportFile, reports, classlistElements) {
		System.out.println("  Outputting report to "
				+ reportFile.getParentAbsolutePath());
		System.out.println("    Aggregating report elements . . .");
		clock.start(MSG_AGGREGATE_TOTAL);
		proceed(reportFile, reports, classlistElements);
		stopClock(MSG_AGGREGATE_TOTAL);
	}

	void around(): call(void outputHtml(File, AggregateReport,
			String) throws IOException) {
		System.out.println("  Creating HTML report . . .");
		clock.start(MSG_HTML_REPORT_TOTAL);
		proceed();
		stopClock(MSG_HTML_REPORT_TOTAL);
	}

	void around(): call(void initialize(ClasslistElement[]) throws IOException) {
		System.out.println("    Initializing the report . . .");
		clock.start(MSG_INIT_REPORT_TOTAL);
		proceed();
		stopClock(MSG_INIT_REPORT_TOTAL);
	}

	void around(ClasslistElementVisitor visitor): initialize(visitor) {
		final ClasslistElementStatistic statisticListener = new ClasslistElementStatistic();
		visitor.addListener(statisticListener);
		proceed(visitor);
		System.out.println("      " + statisticListener + " encountered.");
	}

	private Clock getClockResource() {
		ResourceLocator locator = ResourceLocator.instance();
		if (!locator.contains("clock")) {
			locator.register("clock", new Clock());
		}
		return (Clock) locator.lookup("clock");
	}

	private void stopClock(final String clockType) {
		clock.stop(clockType);
		System.out.println(clock.toString(clockType));
	}

}
