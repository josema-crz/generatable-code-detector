package namesimilarity.heuristics;

import namesimilarity.NameSimilarityHeuristic;

/**
 * !!! This is an example of how an heuristic would be defined. The
 * implementation is not complete.
 * 
 * Represents an heuristic that tries to do the replacement even if the strings
 * do not match exactly, that is, when a certain number of characters differ.
 */
public class NameSimilarityHeuristicInexactMatch extends NameSimilarityHeuristic {
	private int differentCharactersAllowed;

	/**
	 * Creates a NameSimilarityHeuristicInexactMatch with the number of
	 * different characters allowed.
	 * 
	 * @param replacementIdentifier
	 *            The replacement identifier.
	 * @param differentCharactersAllowed
	 *            The number of different characters allowed.
	 */
	public NameSimilarityHeuristicInexactMatch(String replacementIdentifier, int differentCharactersAllowed) {
		super(replacementIdentifier);
		this.differentCharactersAllowed = differentCharactersAllowed;
	}

	/**
	 * {@inheritDoc}
	 */
	public NameSimilarityHeuristicInexactMatch(String replacementIdentifier) {
		super(replacementIdentifier);
		differentCharactersAllowed = 1; // Default value.
	}

	/**
	 * Does not perform any transformation.
	 */
	@Override
	public String transformString(String s) {
		return s;
	}

	/**
	 * Does a simple replacement using the method in the java String class.
	 */
	@Override
	public String getReplacedString(String original, String replacement) {
		// TODO Divide the String in its compounding words, according to the
		// Java naming conventions (CamelCase).

		// TODO Check the number of different characters between each word and
		// the replacement string. If it is below the threshold, do the
		// replacement.

		// TODO Concatenate again the words and return the resulting string.

		return null;
	}
}
