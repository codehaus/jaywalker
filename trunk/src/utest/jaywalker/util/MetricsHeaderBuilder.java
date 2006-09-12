package jaywalker.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jaywalker.html.HtmlBuilder;

public class MetricsHeaderBuilder {

	private static final HtmlBuilder OUTPUTTER_HTML = new HtmlBuilder();

	private final String scope;

	public MetricsHeaderBuilder(String scope) {
		this.scope = scope;
	}

	private String toolTip(String value, String tipTitle, String tipValue) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OUTPUTTER_HTML.toolTip(baos, value, tipTitle, tipValue);
			baos.flush();
			baos.close();
			return baos.toString();
		} catch (IOException e) {
			throw new RuntimeException("Error creating header value", e);
		}
	}

	public String[] build() {
		return new String[] { scope, totalClassesHeader(),
				abstractClassesHeader(), abstractnessHeader(),
				afferentHeader(), efferentHeader(), instabilityHeader(),
				distanceHeader() };
	}

	private String toSingleOccurrence(String value) {
		String lowerValue = value.toLowerCase();
		if (lowerValue.startsWith("a")) {
			return "an " + lowerValue;
		}
		return "a " + lowerValue;
	}

	private String toMultipleOccurrences(String value) {
		return value.toLowerCase() + "s";
	}

	private String totalClassesHeader() {
		return toolTip(
				"Total Classes",
				"Total Classes",
				"The total number of concrete and abstract classes<br/>and interfaces in "
						+ toSingleOccurrence(scope)
						+ ".</p><p>This number represents the extensibility of the<br/>"
						+ scope.toLowerCase() + ".");
	}

	private String abstractClassesHeader() {
		return toolTip(
				"Abstract Classes",
				"Abstract Classes",
				"The total number of abstract classes and interfaces<br/>in "
						+ toSingleOccurrence(scope)
						+ ".</p><p>This number also represents the extensibility of<br/>the "
						+ scope.toLowerCase() + ".");
	}

	private String abstractnessHeader() {
		return toolTip(
				"Abstractness",
				"Abstractness (A)",
				"The ratio of the number of abstract classes and<br/>interfaces to the total number of classes.");
	}

	private String afferentHeader() {
		return toolTip("Afferent", "Afferent Couplings (Ca)",
				"The number of other " + toMultipleOccurrences(scope)
						+ " that depend upon<br/>classes within the "
						+ scope.toLowerCase()
						+ ".</p><p>This metric is an indicator of the "
						+ toMultipleOccurrences(scope) + "<br/>responsibility.");
	}

	private String efferentHeader() {
		return toolTip(
				"Efferent",
				"Efferent Couplings (Ce)",
				"The number of other "
						+ toMultipleOccurrences(scope)
						+ " that the classes<br/>within the "
						+ scope.toLowerCase()
						+ " depend upon.</p><p>This metric is an indicator of the "
						+ toMultipleOccurrences(scope) + "<br/>independence.");
	}

	private String instabilityHeader() {
		return toolTip(
				"Instability",
				"Instability (I)",
				"The ratio of efferent coupling (Ce) to total coupling<br/>(Ce + Ca) such that I = Ce / (Ce + Ca).</p><p>This ratio is an indicator of the "
						+ toMultipleOccurrences(scope)
						+ " resilience<br/>to change.</p><p>The range for this metric is 0 to 1, with I = 0<br/>indicating a completely stable "
						+ scope.toLowerCase()
						+ " and I = 1<br/>indicating a completely instable "
						+ scope.toLowerCase());
	}

	private String distanceHeader() {
		return toolTip(
				"Distance",
				"Distance from the Main Sequence (D)",
				"The perpendicular distance of "
						+ toSingleOccurrence(scope)
						+ " from the<br/>idealized line A + I = 1.</p><p>This metric is an indicator of the "
						+ toMultipleOccurrences(scope)
						+ " balance<br/>between abstractness and stability.</p><p>Ideal "
						+ toMultipleOccurrences(scope)
						+ " are either completely abstract and<br/>stable (x=0, y=1) or completely concrete and instable<br/>(x=1, y=0).</p><p>The range for this metric  is 0 to 1, with D=0<br/>indicating "
						+ toSingleOccurrence(scope)
						+ " that is coincident with the<br/>main sequence and D=1 indicating "
						+ toSingleOccurrence(scope)
						+ " that is<br/>as far from the main sequence as possible.");

	}

}
