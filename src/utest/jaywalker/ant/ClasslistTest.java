package jaywalker.ant;

import junit.framework.TestCase;

public class ClasslistTest extends TestCase {
	public void testShouldCreateTaskWithDefaultType() {
		Classlist classlist = new Classlist();
		assertSame(Classlist.TYPE_DEEP, classlist.getType());
	}
	
	public void testShouldCreateTaskWithDeepType() {
		Classlist classlist = new Classlist();
		classlist.setType(new String("deep"));
		assertSame(Classlist.TYPE_DEEP, classlist.getType());
	}
	
	public void testShouldCreateTaskWithShallowType() {
		Classlist classlist = new Classlist();
		classlist.setType(new String("shallow"));
		assertSame(Classlist.TYPE_SHALLOW, classlist.getType());
	}
	
	public void testShouldCreateTaskWithSystemType() {
		Classlist classlist = new Classlist();
		classlist.setType(new String("system"));
		assertSame(Classlist.TYPE_SYSTEM, classlist.getType());
	}

	public void testShouldCreateTaskWithDefaultTypeOnIllegalTypes() {
		assertEqualsDefault("bogus");
		assertEqualsDefault("deeP");
		assertEqualsDefault("ShAlLoW");
	}

	private void assertEqualsDefault(String type) {
		Classlist classlist = new Classlist();
		classlist.setType(type);
		assertSame(Classlist.TYPE_DEEP, classlist.getType());
	}
	
}
