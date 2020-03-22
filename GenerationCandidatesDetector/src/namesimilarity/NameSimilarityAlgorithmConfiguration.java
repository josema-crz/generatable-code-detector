package namesimilarity;

import java.util.LinkedList;
import java.util.List;

/**
 * Configuration values to be used in the name similarity calculation process.
 */
public class NameSimilarityAlgorithmConfiguration {
	/** Identifier to be used during the replacements. */
	private String replacementIdentifier;

	/**
	 * Optional list of heuristics to be applied on the names when the
	 * replacement can not be directly done.
	 */
	private List<NameSimilarityHeuristic> heuristics;

	/**
	 * Creates a NameSimilarityAlgorithmConfiguration with the default values:
	 * <ul>
	 * <li>"X" as the replacement identifier.</li>
	 * <li>An empty list of heuristics.</li>
	 * </ul>
	 * 
	 * @return A default configuration.
	 */
	public static NameSimilarityAlgorithmConfiguration getDefaultConfiguration() {
		NameSimilarityAlgorithmConfiguration defaultConfig = new NameSimilarityAlgorithmConfiguration();
		defaultConfig.setReplacementIdentifier("X");
		defaultConfig.setHeuristics(new LinkedList<NameSimilarityHeuristic>());
		
		return defaultConfig;
	}

	public String getReplacementIdentifier() {
		return replacementIdentifier;
	}

	public void setReplacementIdentifier(String replacementIdentifier) {
		this.replacementIdentifier = replacementIdentifier;
	}

	public List<NameSimilarityHeuristic> getHeuristics() {
		return new LinkedList<NameSimilarityHeuristic>(heuristics);
	}

	public void setHeuristics(List<NameSimilarityHeuristic> heuristics) {
		this.heuristics = new LinkedList<NameSimilarityHeuristic>(heuristics);
	}

	public void addHeuristic(NameSimilarityHeuristic heuristic) {
		heuristics.add(heuristic);
	}

	public void removeHeuristic(NameSimilarityHeuristic heuristic) {
		heuristics.remove(heuristic);
	}	
}
