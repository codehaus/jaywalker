package jaywalker.html;

import java.io.IOException;

import jaywalker.xml.bind.TabPage;


public interface Content {
	byte[] create(TabPage tabPage) throws IOException;
}
