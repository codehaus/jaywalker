package jaywalker.util;

public class DotOutputterFactory {

	public Outputter create(String filename) {
		String value = System.getProperty("DotOutputter");
		if ( StubOutputter.class.getName().equals(value)) {
			return new StubOutputter();
		}
		return new DotOutputter(filename);
	}

}
