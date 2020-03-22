package namesimilarity;

import java.util.List;

/**
 * Interface to be implemented by any algorithm that implements the name
 * similarity calculation process.
 */
public interface INameSimilarityAlgorithm {
	/**
	 * Obtains a set of groups of related classes from the analysis of their
	 * names.
	 * 
	 * @param names
	 *            Names of the files to be analyzed.
	 * @param replacementNames
	 *            Names to be used during the analysis to perform the
	 *            replacements.
	 * @return A result containing the groups of related classes.
	 */
	public NameSimilarityResult calculateNameSimilarity(List<String> names, List<String> replacementNames);

	/**
	 * Obtains a set of groups of related classes from the analysis of their
	 * names.
	 * 
	 * @param names
	 *            Names of the files to be analyzed.
	 * @return A result containing the groups of related classes.
	 */
	public NameSimilarityResult calculateNameSimilarity(List<String> names);
}
