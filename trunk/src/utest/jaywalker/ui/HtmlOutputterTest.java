package jaywalker.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import junit.framework.TestCase;

public class HtmlOutputterTest extends TestCase {

	private static final Content DEFAULT_CONTENT = new Content() {
		public byte[] getBytes() throws IOException {
			return "(content)".getBytes();
		}
	};

	public interface Executor {
		void execute(OutputStream os) throws IOException;
	}

	public void testShouldCreateIndex() throws IOException {
		final HtmlOutputter html = new HtmlOutputter() {
			public void docType(OutputStream os) throws IOException {
				os.write("(docType)".getBytes());
			}

			public void html(OutputStream os, Content[] contents)
					throws IOException {
				for (int i = 0; i < contents.length; i++) {
					os.write(contents[i].getBytes());
				}
			}
		};
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.index(os, new Content[] { DEFAULT_CONTENT });
			}
		};
		String expected = "(docType)(content)";
		assertEquals(expected, executor);
	}

	public void testShouldCreateHtmlTags() throws IOException {
		final HtmlOutputter html = new HtmlOutputter() {
			public void head(OutputStream os) throws IOException {

			}

			public void body(OutputStream os, Content[] contents)
					throws IOException {

			}
		};
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.html(os, null);
			}
		};
		String expected = "<html></html>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateDocTypeTags() throws IOException {
		final HtmlOutputter html = new HtmlOutputter();
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.docType(os);
			}
		};
		String expected = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
				+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n";
		assertEquals(expected, executor);
	}

	public void testShouldCreateHeadTags() throws IOException {
		final HtmlOutputter html = new HtmlOutputter() {
			public void title(OutputStream os) throws IOException {
				os.write("(title)".getBytes());
			}

			public void meta(OutputStream os, String httpEquiv,
					String content) throws IOException {
				os.write(("(" + content + ")").getBytes());
			}

			public void javascript(OutputStream os, String src)
					throws IOException {
				os.write(("(" + src + ")").getBytes());
			}

			public void css(OutputStream os, String href) throws IOException {
				os.write(("(" + href + ")").getBytes());
			}
		};
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.head(os);
			}
		};
		String expected = "<head>(title)(text/html; charset=utf-8)"
				+ "(local/webfxlayout.js)(local/webfxapi.js)"
				+ "(js/tabpane.js)(js/stringbuilder.js)(js/sortabletable.js)"
				+ "(js/domLib.js)(js/fadomatic.js)(js/domTT.js)"
				+ "(css/tab.css)(css/sortabletable.css)(css/dom.css)(css/override.css)</head>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateTitleTags() throws IOException {
		final HtmlOutputter html = new HtmlOutputter();
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.title(os);
			}
		};
		String expected = "<title>JayWalker Report</title>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateMetaTags() throws IOException {
		final HtmlOutputter html = new HtmlOutputter();
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.meta(os, "httpEquiv", "content");
			}
		};
		String expected = "<meta http-equiv=\"httpEquiv\" content=\"content\" />";
		assertEquals(expected, executor);
	}

	public void testShouldCreateJavaScriptTag() throws IOException {
		final HtmlOutputter html = new HtmlOutputter();
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.javascript(os, "src");
			}
		};
		String expected = "<script type=\"text/javascript\" src=\"src\"></script>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateCssTag() throws IOException {
		final HtmlOutputter html = new HtmlOutputter();
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.css(os, "href");
			}
		};
		String expected = "<link type=\"text/css\" rel=\"stylesheet\" href=\"href\" />";
		assertEquals(expected, executor);
	}

	public void testShouldCreateBodyTag() throws IOException {
		final HtmlOutputter html = new HtmlOutputter() {
			public void h2(OutputStream os, String value) throws IOException {
				os.write(("(" + value + ")").getBytes());
			}

			public void javascript(OutputStream os, String src)
					throws IOException {
				os.write(("(" + src + ")").getBytes());
			}
		};
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.body(os, new Content[] { DEFAULT_CONTENT });
			}
		};
		String expected = "<body>(JayWalker Report)(js/tablesetup.js)(content)</body>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateH2Tag() throws IOException {
		final HtmlOutputter html = new HtmlOutputter();
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.h2(os, "(value)");
			}
		};
		String expected = "<h2>(value)</h2>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateH2TagWithClass() throws IOException {
		final HtmlOutputter html = new HtmlOutputter();
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.h2(os, "(class)", "(value)");
			}
		};
		String expected = "<h2 class=\"(class)\">(value)</h2>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateDivTag() throws IOException {
		final HtmlOutputter html = new HtmlOutputter();
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.div(os, "(class)", "(id)", "(value)".getBytes());
			}
		};
		String expected = "<div class=\"(class)\" id=\"(id)\">(value)</div>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateJavaScriptTagWithValues() throws IOException {
		final HtmlOutputter html = new HtmlOutputter();
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.javascript(os, new String[] { "(value1)", "(value2)" });
			}
		};
		String expected = "<script type=\"text/javascript\">\n(value1)\n(value2)\n</script>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateTableTag() throws IOException {
		final HtmlOutputter html = new HtmlOutputter() {
			public void thead(OutputStream os, String[] headers)
					throws IOException {
				os.write("(thead)".getBytes());
			}

			public void tbody(OutputStream os, String[][] values)
					throws IOException {
				os.write("(tbody)".getBytes());
			}

		};
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.table(os, "(id)", "(class)", null, null);
			}
		};
		String expected = "<table class=\"(class)\" id=\"(id)\">(thead)(tbody)</table>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateTableHead() throws IOException {
		final HtmlOutputter html = new HtmlOutputter() {
			public void tr(OutputStream os, String clazz, String[] values) throws IOException {
				for (int i = 0; i < values.length; i++) {
					os.write(values[i].getBytes());
				}
			}
		};
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.thead(os, new String[] { "(head1)", "(head2)" });
			}
		};
		String expected = "<thead>(head1)(head2)</thead>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateTableRow() throws IOException {
		final HtmlOutputter html = new HtmlOutputter() {
			public void td(OutputStream os, String value) throws IOException {
				os.write(value.getBytes());
			}
		};
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.tr(os, "na",  new String[] { "(value1)", "(value2)" });
			}
		};
		String expected = "<tr class=\"na\">(value1)(value2)</tr>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateTableCell() throws IOException {
		final HtmlOutputter html = new HtmlOutputter();
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.td(os, "(value1)");
			}
		};
		String expected = "<td>(value1)</td>";
		assertEquals(expected, executor);
	}

	public void testShouldCreateTableBody() throws IOException {
		final HtmlOutputter html = new HtmlOutputter() {
			public void tr(OutputStream os, String clazz, String[] values) throws IOException {
				for (int i = 0; i < values.length; i++) {
					os.write(values[i].getBytes());
				}
			}
		};
		Executor executor = new Executor() {
			public void execute(OutputStream os) throws IOException {
				html.tbody(os, new String[][] { { "(row11)", "(row12)" },
						{ "(row21)", "(row22)" } });
			}
		};
		String expected = "<tbody>(row11)(row12)(row21)(row22)</tbody>";
		assertEquals(expected, executor);
	}

	private void assertEquals(String expected, Executor executor)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		executor.execute(baos);
		baos.flush();
		baos.close();
		String actual = new String(baos.toByteArray());
		assertEquals(expected, actual);
	}

}
