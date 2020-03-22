package clonedetection;

import java.util.List;

/**
 * Defines the functionality that a class implementing the clone detection
 * feature should provide.
 */
public interface CloneDetector {
	/**
	 * Returns the result of the clone detection process on a list of files.
	 * 
	 * @param files
	 *            The paths of the files where the clones are searched
	 * @return A result containing all the groups of clones found in the files
	 */
	public CloneDetectionResult detectClones(List<String> files);
}
