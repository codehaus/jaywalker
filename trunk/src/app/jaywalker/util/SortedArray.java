package jaywalker.util;

import java.util.Arrays;

public class SortedArray {

	private final Object[] model;

	public SortedArray(String[] sortedArray) {
		model = sortedArray;
	}

	public static SortedArray valueOf(String[] unsortedArray) {
		String[] sortedArray = new String[unsortedArray.length];
		System.arraycopy(unsortedArray, 0, sortedArray, 0,
						unsortedArray.length);
		Arrays.sort(sortedArray);
		return new SortedArray(sortedArray);
	}

	public boolean contains(String value) {
		return Arrays.binarySearch(model, value) >= 0;
	}

}
