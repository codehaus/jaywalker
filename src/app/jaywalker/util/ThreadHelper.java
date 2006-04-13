package jaywalker.util;

import java.net.URL;


public class ThreadHelper {

	public void verify(URL url) {
		ResourceLocator locator = ResourceLocator.instance();
		final String key = toThreadKey(url);
		if (locator.contains(key)) {
			Thread thread = (Thread) locator.lookup(key);
			waitOn(thread);
			locator.clear(key);
		}
	}

	private void waitOn(Thread thread) {
		if (thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
		}
	}

	public void start(Runnable runnable, URL url) {
		Thread thread = new Thread(runnable);
		ResourceLocator.instance()
				.register(toThreadKey(url), thread);
		thread.start();
	}

	private String toThreadKey(URL url) {
		return "thread:" + url;
	}

}
