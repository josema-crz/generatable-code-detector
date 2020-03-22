package utils;

import java.io.File;

import org.eclipse.core.resources.IFile;

/**
 * Helper methods related to file management.
 */
public class FileUtils {
	/**
	 * Returns the simple name of a file, that is, the name without the path or
	 * the extension.
	 * 
	 * @param file
	 *            The file.
	 * @return The simple name of the file as previously explained.
	 */
	public static String getSimpleName(IFile file) {
		// Get the name without the extension.
		String simpleName = file.getName();
		String extension = file.getFileExtension();
		if (extension != null) {
			// -1 because of the dot before the extension.
			simpleName = simpleName.substring(0, simpleName.length() - extension.length() - 1);
		}
		return simpleName;
	}

	/**
	 * Returns the simple name of a file full name, that is, the name without
	 * the path or the extension.
	 * 
	 * @param fileName
	 *            The file full name.
	 * @return The simple name of the file as previously explained.
	 */
	public static String getSimpleName(String fileName) {
		// Get the name without the path.
		int lastIndex = fileName.lastIndexOf(File.separator);
		if (lastIndex != -1) {
			fileName = fileName.substring(lastIndex + 1, fileName.length());
		}

		// Get the name without the extension.
		lastIndex = fileName.lastIndexOf('.');
		if (lastIndex != -1) {
			fileName = fileName.substring(0, lastIndex);
		}
		return fileName;
	}
}
