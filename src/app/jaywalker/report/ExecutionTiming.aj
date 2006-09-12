package jaywalker.report;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import jaywalker.classlist.ClasslistElement;
import jaywalker.util.Clock;
import jaywalker.util.FileDecorator;
import jaywalker.util.ResourceLocator;

public aspect ExecutionTiming {

	private static final String MSG_JAYWALKER_REPORT_TOTAL = "Total time to create the JayWalker report";
	private static final String MSG_XML_REPORT_TOTAL = "    Total time to create XML report";
	private static final String MSG_INIT_REPORT_TOTAL = "      Time to initialize the report";
	private static final String MSG_HTML_REPORT_TOTAL = "    Time to create HTML report";
	private static final String MSG_AGGREGATE_TOTAL = "      Time to aggregate report elements";
	private final Clock clock = getClockResource();

	before(): call(void execute(String, Properties, File,
			String) throws IOException) {
		System.out.println("Creating the JayWalker report . . .");
		clock.start(MSG_JAYWALKER_REPORT_TOTAL);
	}

	after(): call(void execute(String, Properties, File,
			String) throws IOException) {
		stopClock(MSG_JAYWALKER_REPORT_TOTAL);
	}


	before(): call(AggregateReport execute(String, Report[],
			FileDecorator) throws IOException) {
		System.out.println("  Creating XML report . . .");
		clock.start(MSG_XML_REPORT_TOTAL);
	}

	after(): call(AggregateReport execute(String, Report[],
			FileDecorator) throws IOException) {
		stopClock(MSG_XML_REPORT_TOTAL);
	}

	before(): call(AggregateReport createAggregateReport(Report[],
		 ClasslistElement[], FileDecorator)
			throws IOException) {
		System.out.println("    Aggregating report elements . . .");
		clock.start(MSG_AGGREGATE_TOTAL);
	}

	after(): call(AggregateReport createAggregateReport(Report[],
			 ClasslistElement[], FileDecorator)
				throws IOException) {
		stopClock(MSG_AGGREGATE_TOTAL);
	}

	before(): call(void outputHtml(File, AggregateReport,
			String) throws IOException) {
		System.out.println("  Creating HTML report . . .");
		clock.start(MSG_HTML_REPORT_TOTAL);
	}

	after(): call(void outputHtml(File, AggregateReport,
			String) throws IOException) {
		stopClock(MSG_HTML_REPORT_TOTAL);
	}

	before(): call(void initReportModels(Report[],
			ClasslistElement[]) throws IOException) {
		System.out.println("    Initializing the report . . .");
		clock.start(MSG_INIT_REPORT_TOTAL);
	}

	after(): call(void initReportModels(Report[],
			ClasslistElement[]) throws IOException) {
		stopClock(MSG_INIT_REPORT_TOTAL);
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
