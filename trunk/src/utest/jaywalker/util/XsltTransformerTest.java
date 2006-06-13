package jaywalker.util;

import jaywalker.classlist.JayWalkerTestCase;

public class XsltTransformerTest extends JayWalkerTestCase {
	public void testCreateEmptyXsltTransformersFromNullAndEmptyStrings() {
		Outputter[] transformers = XsltTransformer.valueOf((String[])null);
		assertEquals(0, transformers.length);
		transformers = XsltTransformer.valueOf(new String[0]);
		assertEquals(0, transformers.length);
	}

	public void testCreateXsltTranformersFromStringsOfOneElement() {
		Outputter transformer = XsltTransformer
				.valueOf("package-dependencies-cycle-html.xslt");
		assertNotNull(transformer);

		String[] filenames = new String[] { "package-dependencies-cycle-html.xslt" };
		Outputter[] transformers = XsltTransformer.valueOf(filenames);
		assertEquals(1, transformers.length);
	}

	public void testCreateXsltTranformersFromStringsOfMultipleElements() {
		String[] filenames = new String[] {
				"package-dependencies-resolved-html.xslt",
				"package-dependencies-resolved-dot.xslt" };
		Outputter[] transformers = XsltTransformer.valueOf(filenames);
		assertEquals(2, transformers.length);
	}

}
