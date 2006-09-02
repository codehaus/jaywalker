package jaywalker.html;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jaywalker.xml.bind.Config;

public class ConfigDecorator {

	private static final HtmlOutputter OUTPUTTER_HTML = new HtmlOutputter();

	private final List tabPaneDecoratorList = new ArrayList();

	private final Config config;

	public ConfigDecorator(Config config) {
		this.config = config;
	}

	public Config getConfig() {
		return config;
	}

	public void add(TabPaneDecorator decorator) {
		tabPaneDecoratorList.add(decorator);
	}

	public void html(OutputStream os) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < tabPaneDecoratorList.size(); i++) {
			TabPaneDecorator tabPaneDecorator = (TabPaneDecorator) tabPaneDecoratorList
					.get(i);
			tabPaneDecorator.html(baos);
		}
		baos.flush();
		baos.close();
		OUTPUTTER_HTML.index(os, baos.toByteArray());
	}

}
