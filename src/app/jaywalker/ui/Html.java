package jaywalker.ui;

import java.io.IOException;
import java.io.OutputStream;

public class Html {

	public void index(OutputStream os, Content content) throws IOException {
		docType(os);
		html(os, content);
	}

	protected void docType(OutputStream os) throws IOException {
		final String docType = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n";
		os.write(docType.getBytes());
	}

	protected void html(OutputStream os, Content content) throws IOException {
		os.write("<html>".getBytes());
		head(os);
		body(os, content);
		os.write("</html>".getBytes());
	}

	protected void head(OutputStream os) throws IOException {
		os.write("<head>".getBytes());
		title(os);
		meta(os, "Content-Type", "text/html; charset=utf-8");
		javascript(os, "local/webfxlayout.js");
		javascript(os, "local/webfxapi.js");
		javascript(os, "js/tabpane.js");
		javascript(os, "js/stringbuilder.js");
		javascript(os, "js/sortabletable.js");
		css(os, "css/tab.css");
		css(os, "css/sortabletable.css");
		css(os, "css/override.css");
		os.write("</head>".getBytes());
	}

	protected void title(OutputStream os) throws IOException {
		os.write("<title>".getBytes());
		os.write(titleValue().getBytes());
		os.write("</title>".getBytes());
	}

	protected String titleValue() {
		return "JayWalker Report";
	}

	protected void meta(OutputStream os, String httpEquiv, String content)
			throws IOException {
		os.write("<meta http-equiv=\"".getBytes());
		os.write(httpEquiv.getBytes());
		os.write(" content=\"".getBytes());
		os.write(content.getBytes());
		os.write("\" />".getBytes());
	}

	protected void javascript(OutputStream os, String src) throws IOException {
		os.write("<script type=\"text/javascript\" ".getBytes());
		os.write("src=\"".getBytes());
		os.write(src.getBytes());
		os.write("\"></script>".getBytes());
	}

	protected void css(OutputStream os, String href) throws IOException {
		os.write("<link type=\"text/css\" ".getBytes());
		os.write("rel=\"stylesheet\" href=\"".getBytes());
		os.write(href.getBytes());
		os.write("\" />".getBytes());
	}

	protected void body(OutputStream os, Content content) throws IOException {
		os.write("<body>".getBytes());
		h2(os, titleValue());
		javascript(os, "js/tablesetup.js");
		os.write(content.getBytes());
		os.write("</body>".getBytes());
	}

	protected void h2(OutputStream os, String value) throws IOException {
		os.write("<h2>".getBytes());
		os.write(value.getBytes());
		os.write("</h2>".getBytes());
	}

	protected void h2(OutputStream os, String clazz, String value)
			throws IOException {
		os.write("<h2 class=\"".getBytes());
		os.write(clazz.getBytes());
		os.write("\">".getBytes());
		os.write(value.getBytes());
		os.write("</h2>".getBytes());
	}

	protected void div(OutputStream os, String clazz, String id, byte[] value)
			throws IOException {
		os.write("<div class=\"".getBytes());
		os.write(clazz.getBytes());
		os.write("\" id=\"".getBytes());
		os.write(id.getBytes());
		os.write("\">".getBytes());
		os.write(value);
		os.write("</div>".getBytes());
	}

	protected void javascript(OutputStream os, String[] values)
			throws IOException {
		os.write("<script type=\"text/javascript\">\n".getBytes());
		for (int i = 0; i < values.length; i++) {
			os.write(values[i].getBytes());
			os.write("\n".getBytes());
		}
		os.write("</script>".getBytes());
	}

}