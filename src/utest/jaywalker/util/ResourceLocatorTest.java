/**
 * Copyright 2005 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jaywalker.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jaywalker.testutil.Path;
import junit.framework.TestCase;

public class ResourceLocatorTest extends TestCase {

	public void tearDown() {
		ResourceLocator.instance().clear();
	}

	public void testShouldReturnValidTempDirOnLookup() {
		final ResourceLocator locator = ResourceLocator.instance();
		locator.clear();
		locator.register("tempDir", Path.DIR_TEMP);
		assertSame(Path.DIR_TEMP, locator.lookup("tempDir"));
	}

	public void testShouldThrowResourceNotFoundExceptionOnLookup() {
		try {
			final ResourceLocator locator = ResourceLocator.instance();
			locator.clear();
			locator.lookup("tempDir");
			fail("ResourceNotFoundException should have been thrown");
		} catch (ResourceNotFoundException e) {
			// do nothing
		}
	}

	public void testShouldFindKeysWithMatchingFilterAsPrefix() {
		String[] inputs = new String[] { "1", "prefix:1", "2", "prefix:2", "3" };
		String[] expected = new String[] { "prefix:1", "prefix:2" };
		Collection collection = Arrays.asList(inputs);
		Collection output = ResourceLocator.instance()
				.findKeysWithMatchingFilterAsPrefix(collection, "prefix:");
		String[] actual = (String[]) output.toArray(new String[output.size()]);
		assertEquals(expected, actual);
	}

	public void testShouldRemoveKeysFromMapWithMatchingFilterAsPrefix() {
		String[] inputs = new String[] { "1", "prefix:1", "2", "prefix:2", "3" };
		String[] expected = new String[] { "1", "2", "3" };
		ResourceLocator locator = ResourceLocator.instance();
		Map backupMap = locator.map;
		locator.map = new HashMap();
		for (int i = 0; i < inputs.length; i++) {
			locator.map.put(inputs[i], inputs[i]);
		}
		try {
			ResourceLocator.instance().unregisterByKeyPrefix("prefix:");
			String[] actual = (String[]) locator.map.keySet().toArray(
					new String[locator.map.size()]);
			Arrays.sort(expected);
			Arrays.sort(actual);
			assertEquals(expected, actual);
		} finally {
			locator.map = backupMap;
		}
	}

	private void assertEquals(Object[] expected, Object[] actual) {
		if (expected == null) {
			assertNull("expected was null but actual was not", actual);
			return;
		}
		assertEquals("expected and actual arrays have differing lengths",
				expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}
}
