package namesimilarity.heuristics;

import namesimilarity.NameSimilarityHeuristic;

/**
 * !!! This is an example of how an heuristic would be defined. The
 * implementation is not complete.
 * 
 * Represents an heuristic that tries to eliminate the differences between
 * different words with the same meaning by using a synonym unique form.
 */
public class NameSimilarityHeuristicSynonym extends NameSimilarityHeuristic {
	/**
	 * {@inheritDoc}
	 */
	public NameSimilarityHeuristicSynonym(String replacementIdentifier) {
		super(replacementIdentifier);
	}

	/**
	 * Divides the string in its compounding words, obtains their general
	 * synonym form and returns them concatenated again.
	 */
	@Override
	public String transformString(String s) {
		// TODO Divide the String in its compounding words, according to the
		// Java naming conventions (CamelCase).

		// TODO Use some algorithm to obtain the unique synonym form for every
		// word. This means that all the words with the same meaning will be
		// replaced with a unique word.

		// TODO Concatenate again the words in the same order and return the
		// resulting string.

		return null;
	}

	/**
	 * Does a simple replacement using the method in the java String class.
	 */
	@Override
	public String getReplacedString(String original, String replacement) {
		return original.replace(replacement, replacementIdentifier);
	}

}
