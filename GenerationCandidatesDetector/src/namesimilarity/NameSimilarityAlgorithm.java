package namesimilarity;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import utils.Logger;

/**
 * Implementation of the name similarity calculation process.
 */
public class NameSimilarityAlgorithm implements INameSimilarityAlgorithm {
	/** Configuration values. */
	private NameSimilarityAlgorithmConfiguration configuration;

	/**
	 * Creates a NameSimilarityAlgorithm with the provided configuration.
	 * 
	 * @param configuration
	 *            Configuration values.
	 */
	public NameSimilarityAlgorithm(NameSimilarityAlgorithmConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Creates a NameSimilarityAlgorithm with the default configuration.
	 */
	public NameSimilarityAlgorithm() {
		this(NameSimilarityAlgorithmConfiguration.getDefaultConfiguration());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NameSimilarityResult calculateNameSimilarity(List<String> names, List<String> replacementNames) {
		Logger.log("Name similarity: sorting the names...");
		
		// Sort the names to be used in the replacements, from the longest to
		// the shortest.
		replacementNames.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return ((Integer) o2.length()).compareTo(o1.length());
			}
		});

		// Build a NameSimilarityResultName for each name.
		List<NameSimilarityResultName> resultNames = new LinkedList<NameSimilarityResultName>();
		for (String name : names) {
			resultNames.add(new NameSimilarityResultName(name));
		}
		
		Logger.log("Name similarity: doing the replacements...");

		// For each name to be used in the replacements, search it in the names
		// collection and replace it whenever possible.
		for (String repName : replacementNames) {
			// TODO Best place to put this here? Configurable?
			// Ignore names that are too short. They tend to produce incorrect
			// results.
			if (repName.length() >= 3) {
				for (NameSimilarityResultName resultName : resultNames) {
					// We must be careful to skip the corresponding name when
					// checking for matches.
					if (!resultName.getModifiedName().equals(repName)) {
						if (resultName.getModifiedName().contains(repName)) {
							resultName.setModifiedName(resultName.getModifiedName().replace(repName,
									configuration.getReplacementIdentifier()));
							resultName.addDerivedFrom(repName);
						}
						// If it was not directly found, make use of the
						// configured heuristics.
						else {
							/*
							 * The heuristics are tried one by one, that is, the
							 * actions performed on the strings by each of them
							 * do not influence the others.
							 * 
							 * If the combination of several heuristics is
							 * desired, a new heuristic must be defined. Check
							 * NameSimilarityHeuristicPluralSynonym for an
							 * example of such a compound heuristic.
							 */
							for (NameSimilarityHeuristic heuristic : configuration.getHeuristics()) {
								String replacedName = heuristic.doReplacement(resultName.getModifiedName(), repName);

								/*
								 * The replacedName will be equal to the
								 * original if no replacements were made,
								 * therefore we need to check if a replacement
								 * was made.
								 */
								if (!replacedName.equals(resultName.getModifiedName())) {
									resultName.setModifiedName(replacedName);
									resultName.addDerivedFrom(repName);
								}
							}
						}
					}
				}
			}
		}
		
		Logger.log("Name simlarity: forming the groups...");

		// Look for identical names and form NameSimilarityResultGroups from
		// them.
		NameSimilarityResult result = new NameSimilarityResult();

		Set<String> uniqueNames = new HashSet<String>();
		for (NameSimilarityResultName resultName : resultNames) {
			String modifiedName = resultName.getModifiedName();
			if (!uniqueNames.add(modifiedName)) {
				// An equal name already exists, we include this ResultName in
				// the corresponding group.
				NameSimilarityResultGroup group = result.getGroup(modifiedName);
				if (group != null) { // It should never be null at this point.
					group.addName(resultName);
				}
			} else if (!resultName.getDerivedFrom().isEmpty()) {
				// If there is no group for this name but the name is related to
				// some other name, then we create a group for it.
				NameSimilarityResultGroup group = new NameSimilarityResultGroup(modifiedName);
				group.addName(resultName);
				result.addGroup(group);
			}
		}

		// Return the final result containing the groups of related files.
		return result;
	}

	/**
	 * If no specific names are provided to do the replacements, all the names
	 * will be taken into consideration.
	 */
	@Override
	public NameSimilarityResult calculateNameSimilarity(List<String> names) {
		return calculateNameSimilarity(names, names);
	}

}
