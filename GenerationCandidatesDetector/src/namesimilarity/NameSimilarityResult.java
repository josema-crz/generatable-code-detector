package namesimilarity;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import generationcandidatesdetector.GenerationCandidatesDetectorResult;
import utils.StringUtils;

/**
 * Represents the overall result of the name similarity calculation process. It
 * contains a set of groups of related file names, indexed by their name.
 */
public class NameSimilarityResult implements GenerationCandidatesDetectorResult {
	/** Groups of related names identified, indexed by their names. */
	private Map<String, NameSimilarityResultGroup> resultGroups;

	/**
	 * Creates a new empty NameSimilarityResult.
	 */
	public NameSimilarityResult() {
		resultGroups = new LinkedHashMap<String, NameSimilarityResultGroup>();
	}

	public Map<String, NameSimilarityResultGroup> getResultGroups() {
		return new LinkedHashMap<String, NameSimilarityResultGroup>(resultGroups);
	}

	public void setResultGroups(Map<String, NameSimilarityResultGroup> resultGroups) {
		this.resultGroups = new LinkedHashMap<String, NameSimilarityResultGroup>(resultGroups);
	}

	public NameSimilarityResultGroup getGroup(String groupName) {
		return resultGroups.get(groupName);
	}

	public void addGroup(NameSimilarityResultGroup group) {
		resultGroups.put(group.getName(), group);
	}
	
	public List<NameSimilarityResultGroup> getGroups() {
		return new LinkedList<NameSimilarityResultGroup>(resultGroups.values());
	}

	/**
	 * Returns a string with a summary of the result, describing each identified
	 * group of related file names.
	 * 
	 * @return The summary.
	 */
	public String getSummary() {
		StringBuilder summary = new StringBuilder();

		summary.append("----- Name similarity results -----");
		summary.append("\n");
		
		for (NameSimilarityResultGroup group : resultGroups.values()) {
			summary.append("Group name: ");
			summary.append(group.getName());
			summary.append("\n");
			summary.append("Contained classes:");
			summary.append("\n");
			
			for (NameSimilarityResultName name : group.getResultNames()) {
				summary.append(name.getOriginalName());
				summary.append(" (derived from ");				
				summary.append(StringUtils.join(name.getDerivedFrom().toArray(), ", "));
				summary.append(")");
				summary.append("\n");
			}
			
			summary.append("-------------------------------");
			summary.append("\n");
		}

		return summary.toString();
	}
}
