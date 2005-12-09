import java.io.IOException;
import java.util.Properties;

import jaywalker.testutil.Path;

import junit.framework.TestCase;

public class JayWalkerTest extends TestCase {

	public void testCreatePropertiesWithNoArguments() {
		JayWalker jayWalker = new JayWalker();
		Properties properties = jayWalker.toProperties(new String[0]);
		assertEquals(0, properties.size());
	}

	private String toKeyValue(String key, String value) {
		return "-" + key + "=" + value;
	}

	public void testCreatePropertiesWithKeyValuePair() {
		JayWalker jayWalker = new JayWalker();
		String key = "key";
		String value = "value";
		String arg = toKeyValue(key, value);
		Properties properties = jayWalker.toProperties(new String[] { arg });
		assertEquals(1, properties.size());
		assertNotNull(properties.get(key));
		assertEquals(value, properties.get(key));
	}

	private String[] createInvalidArguments() {
		return new String[] { null, "invalid", "invalid=invalid", "-invalid", };
	}

	public void testCreatePropertiesWithInvalidKeyValuePairs() {
		JayWalker jayWalker = new JayWalker();
		Properties properties = jayWalker
				.toProperties(createInvalidArguments());
		assertEquals(0, properties.size());
	}

	public void testCreatePropertiesWithCompoundValue() {
		JayWalker jayWalker = new JayWalker();
		String key = "key";
		String value = "value1,value2,value3";
		String arg = toKeyValue(key, value);
		Properties properties = jayWalker.toProperties(new String[] { arg });
		assertEquals(1, properties.size());
		assertNotNull(properties.getProperty(key));
		assertEquals(value, properties.getProperty(key));
	}

	public void testCreatePropertiesWithMultipleSameKeys() {
		JayWalker jayWalker = new JayWalker();
		String key = "key";
		String value1 = "value1";
		String value2 = "value2";
		String value3 = "value3";
		String expected = "value1,value2,value3";
		String arg1 = toKeyValue(key, value1);
		String arg2 = toKeyValue(key, value2);
		String arg3 = toKeyValue(key, value3);
		Properties properties = jayWalker.toProperties(new String[] { arg1,
				arg2, arg3 });
		assertEquals(1, properties.size());
		assertNotNull(properties.getProperty(key));
		assertEquals(expected, properties.getProperty(key));
	}

	public void testShouldPrintUsages() throws IOException {
		JayWalker jayWalker = new JayWalker();
		try {
			jayWalker.execute(new String[0]);
			fail("IllegalArgumentException expected.");
		} catch (IllegalArgumentException e) {
			JayWalker.printUsage();
		}
	}

	public void testShouldExecuteJayWalkerOnTest1JarSuccessfully()
			throws IOException {
		JayWalker jayWalker = new JayWalker();
		jayWalker.execute(new String[] { "-classlist="
				+ Path.FILE_TEST1_JAR.getAbsolutePath() });
	}

}
