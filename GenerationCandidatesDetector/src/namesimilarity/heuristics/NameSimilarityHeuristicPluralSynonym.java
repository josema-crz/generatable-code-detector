package namesimilarity.heuristics;

import namesimilarity.NameSimilarityHeuristic;

/**
 * !!! This is an example of how an heuristic would be defined. The
 * implementation is not complete. In particular, this example shows how a new
 * heuristic can be easily defined as the composition of other heuristics.
 * 
 * Represents an heuristic that tries to eliminate the differences between the
 * plural and singular forms of the words, as well as between synonyms.
 */
public class NameSimilarityHeuristicPluralSynonym extends NameSimilarityHeuristic {
	/** Heuristics to be used. */
	private NameSimilarityHeuristic pluralHeuristic, synonymHeuristic;

	/**
	 * {@inheritDoc}
	 */
	public NameSimilarityHeuristicPluralSynonym(String replacementIdentifier) {
		super(replacementIdentifier);
		this.pluralHeuristic = new NameSimilarityHeuristicPlural(replacementIdentifier);
		this.synonymHeuristic = new NameSimilarityHeuristicSynonym(replacementIdentifier);
	}

	/**
	 * Consecutively applies the transformations of the contained heuristics.
	 */
	@Override
	public String transformString(String s) {
		String s2 = pluralHeuristic.transformString(s);
		return synonymHeuristic.transformString(s2);
	}

	/**
	 * Consecutively invokes the replacements of the contained heuristics.
	 */
	@Override
	public String getReplacedString(String original, String replacement) {
		// In this case, both heuristics just invoke the java String replace
		// method so it is not actually necessary to use both consecutively. But
		// the implementation of the individual heuristics could change in the
		// future, so it is a good practice to do it like this.
		String s2 = pluralHeuristic.getReplacedString(original, replacement);
		return synonymHeuristic.getReplacedString(s2, replacement);
	}

}
