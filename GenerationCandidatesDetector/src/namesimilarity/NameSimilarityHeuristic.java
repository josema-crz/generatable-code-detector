package namesimilarity;

/**
 * Represents an heuristic to be used during the name similarity calculation
 * process. An heuristic is used when the replacement string is not directly
 * found in the original string. The idea is to apply additional actions on the
 * strings, and/or to modify the way the replacement string is searched in the
 * original string, to increase the possibilities of doing the replacement.
 */
public abstract class NameSimilarityHeuristic {
	/** Identifier to be used in the replacements. */
	protected String replacementIdentifier;

	/**
	 * Creates an heuristic where the provided identifier will be used in the
	 * replacements.
	 * 
	 * @param replacementIdentifier
	 *            The replacement identifier.
	 */
	public NameSimilarityHeuristic(String replacementIdentifier) {
		this.replacementIdentifier = replacementIdentifier;
	}

	/**
	 * Applies some (optional) transformations on the original and replacement
	 * strings and then tries to do the replacement. If the replacement string
	 * is not found in the original string, the original string is returned with
	 * its original value. If the replacement gets done, the resulting string
	 * after the replacement is returned.
	 * 
	 * @param original
	 *            Original string.
	 * @param replacement
	 *            String to be searched and replaced in the original string.
	 * @return The resulting string after the replacement, or the original if
	 *         there was no replacement.
	 */
	public String doReplacement(String original, String replacement) {
		String originalTransformed = transformString(original);
		String replacementTransformed = transformString(replacement);

		String result = getReplacedString(originalTransformed, replacementTransformed);

		return result;
	}

	/**
	 * Obtains a transformed representation of the string. The goal of an
	 * heuristic should be to obtain a more general or common form of the string
	 * so that the probability of the original and replacement strings matching
	 * increases.
	 * 
	 * @param s
	 *            The string.
	 * @return The transformed string according to the heuristic.
	 */
	public abstract String transformString(String s);

	/**
	 * Obtains the string once the replacement has been done. This replacement
	 * can be direct (using the java String replace method) or include
	 * additional behaviour specific to the heuristic.
	 * 
	 * @param original
	 *            Original string.
	 * @param replacement
	 *            String to be searched and replaced in the original string.
	 * @return The string after the replacement has been done, which may be the
	 *         original string if no replacement was done.
	 */
	public abstract String getReplacedString(String original, String replacement);
}
