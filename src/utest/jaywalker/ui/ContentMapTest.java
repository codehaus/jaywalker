package jaywalker.ui;

import junit.framework.TestCase;

public class ContentMapTest extends TestCase {
	public void testShouldAddAndRetrieveSameInstanceFromMap() {
		ContentMap contentMap = new ContentMap();
		assertNull(contentMap.get("scope", "type"));
		byte[] bs = new byte[0];
		contentMap.put("scope", "type", bs);
		assertSame(bs, contentMap.get("scope", "type"));
	}
}
