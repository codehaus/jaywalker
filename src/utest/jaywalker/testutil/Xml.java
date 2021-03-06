package jaywalker.testutil;

public class Xml {

	public static final String XML_TEST1_TEST2_JARS = "<?xml version=\"1.0\"?>"
			+ "<report>"
			+ "  <container type=\"archive\" url=\"file:/C:/JayWalker/build/data/jaywalker-test1.jar\" value=\"\">"
			+ "    <dependency type=\"resolved\" value=\"2\">"
			+ "      <container type=\"archive\" url=\"file:/C:/JayWalker/build/data/jaywalker-test2.jar\" value=\"jaywalker-test2.jar\"/>"
			+ "      <container type=\"archive\" url=\"file:/C:/Java/j2sdk1.4.2_12/jre/lib/rt.jar\" value=\"rt.jar\"/>"
			+ "    </dependency>"
			+ "    <dependency type=\"resolved\" value=\"3\">"
			+ "      <container type=\"package\" value=\"java.lang\"/>"
			+ "      <container type=\"package\" value=\"java.io\"/>"
			+ "      <container type=\"package\" value=\"\"/>"
			+ "    </dependency>"
			+ "    <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test1.jar!/AnotherSerializableImpl.class\" value=\"AnotherSerializableImpl\">"
			+ "        <conflict type=\"serialVersionUid\" value=\"4621090854018986691\"/>"
			+ "      <collision url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/AnotherSerializableImpl.class\">"
			+ "        <conflict type=\"serialVersionUid\" value=\"5167939453010968824\"/>"
			+ "      </collision>"
			+ "    </element>"
			+ "    <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test1.jar!/ChildSerializableImpl.class\" value=\"ChildSerializableImpl\">"
			+ "      <collision url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/ChildSerializableImpl.class\"/>"
			+ "    </element>"
			+ "    <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test1.jar!/GrandChildSerializableImpl.class\" value=\"GrandChildSerializableImpl\"/>"
			+ "    <container type=\"directory\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test1.jar!/META-INF/\">"
			+ "      <element type=\"unknown\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test1.jar!/META-INF/MANIFEST.MF\"/>"
			+ "    </container>"
			+ "    <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test1.jar!/NotSerializable.class\" value=\"NotSerializable\"/>"
			+ "    <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test1.jar!/SerializableImpl.class\" value=\"SerializableImpl\">"
			+ "        <conflict type=\"serialVersionUid\" value=\"1\"/>"
			+ "      <collision url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/SerializableImpl.class\">"
			+ "        <conflict type=\"serialVersionUid\" value=\"2\"/>"
			+ "      </collision>"
			+ "    </element>"
			+ "  </container>"
			+ "  <container type=\"archive\" url=\"file:/C:/JayWalker/build/data/jaywalker-test2.jar\" value=\"\">"
			+ "    <dependency type=\"resolved\" value=\"2\">"
			+ "      <container type=\"archive\" url=\"file:/C:/JayWalker/build/data/jaywalker-test1.jar\" value=\"jaywalker-test1.jar\"/>"
			+ "      <container type=\"archive\" url=\"file:/C:/Java/j2sdk1.4.2_12/jre/lib/rt.jar\" value=\"rt.jar\"/>"
			+ "    </dependency>"
			+ "    <dependency type=\"resolved\" value=\"4\">"
			+ "      <container type=\"package\" value=\"java.lang\"/>"
			+ "      <container type=\"package\" value=\"java.util\"/>"
			+ "      <container type=\"package\" value=\"java.io\"/>"
			+ "      <container type=\"package\" value=\"\"/>"
			+ "    </dependency>"
			+ "    <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/AnotherSerializableImpl.class\" value=\"AnotherSerializableImpl\">"
			+ "        <conflict type=\"serialVersionUid\" value=\"5167939453010968824\"/>"
			+ "      <collision url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test1.jar!/AnotherSerializableImpl.class\">"
			+ "        <conflict type=\"serialVersionUid\" value=\"4621090854018986691\"/>"
			+ "      </collision>"
			+ "    </element>"
			+ "    <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/ChildSerializableImpl.class\" value=\"ChildSerializableImpl\">"
			+ "      <collision url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test1.jar!/ChildSerializableImpl.class\"/>"
			+ "    </element>"
			+ "    <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/Dependency.class\" value=\"Dependency\">"
			+ "      <dependency type=\"unresolved\" value=\"bogus.BogusDependency\"/>"
			+ "    </element>"
			+ "    <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/Dependency2.class\" value=\"Dependency2\">"
			+ "      <dependency type=\"unresolved\" value=\"bogus.BogusDependency2\"/>"
			+ "    </element>"
			+ "    <container type=\"directory\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/META-INF/\">"
			+ "      <element type=\"unknown\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/META-INF/MANIFEST.MF\"/>"
			+ "    </container>"
			+ "    <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/SerializableImpl.class\" value=\"SerializableImpl\">"
			+ "        <conflict type=\"serialVersionUid\" value=\"2\"/>"
			+ "      <collision url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test1.jar!/SerializableImpl.class\">"
			+ "        <conflict type=\"serialVersionUid\" value=\"1\"/>"
			+ "      </collision>"
			+ "    </element>"
			+ "    <container type=\"directory\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/cycle1/\" value=\"cycle1\">"
			+ "      <dependency type=\"resolved\" value=\"2\">"
			+ "        <container type=\"package\" value=\"java.lang\"/>"
			+ "        <container type=\"package\" value=\"cycle2\"/>"
			+ "      </dependency>"
			+ "      <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/cycle1/Node1.class\" value=\"cycle1.Node1\"/>"
			+ "    </container>"
			+ "    <container type=\"directory\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/cycle2/\" value=\"cycle2\">"
			+ "      <dependency type=\"resolved\" value=\"2\">"
			+ "        <container type=\"package\" value=\"cycle3\"/>"
			+ "        <container type=\"package\" value=\"java.lang\"/>"
			+ "      </dependency>"
			+ "      <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/cycle2/Node2.class\" value=\"cycle2.Node2\"/>"
			+ "    </container>"
			+ "    <container type=\"directory\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/cycle3/\" value=\"cycle3\">"
			+ "      <dependency type=\"resolved\" value=\"2\">"
			+ "        <container type=\"package\" value=\"java.lang\"/>"
			+ "        <container type=\"package\" value=\"cycle1\"/>"
			+ "      </dependency>"
			+ "      <element type=\"class\" url=\"jar:file:/C:/JayWalker/build/data/jaywalker-test2.jar!/cycle3/Node3.class\" value=\"cycle3.Node3\">"
			+ "        <dependency type=\"unresolved\" value=\"bogus.BogusDependency\"/>"
			+ "      </element>" + "    </container>" + "  </container>"
			+ "</report>";

}
