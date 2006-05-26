package jaywalker.util;

import java.net.URL;
import java.util.Collection;

public class CollectionHelper {
	public URL[] toURLs(Collection collection) {
		return (URL[]) collection.toArray(new URL[collection.size()]);
	}

	public String[] toStrings(Collection collection) {
		return (String[]) collection.toArray(new String[collection.size()]);
	}
}
