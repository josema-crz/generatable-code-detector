package utils;

/**
 * Includes some string-related utility methods.
 */
public class StringUtils {
	/**
	 * Returns a string with the elements of an array separated by the separator
	 * string.
	 * 
	 * @param array
	 *            Array of elements.
	 * @param separator
	 *            String that will separate the elements in the array.
	 * @return The string as specified.
	 */
	public static String join(Object[] array, String separator) {
		if (array.length == 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		int i;
		for (i = 0; i < array.length - 1; i++) {
			result.append(array[i].toString());
			result.append(separator);
		}
		result.append(array[i].toString());

		return result.toString();
	}
}
