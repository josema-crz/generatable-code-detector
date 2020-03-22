package namesimilarity;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a group of related files, represented by NameSimilarityResultName
 * objects.
 */
public class NameSimilarityResultGroup {
	/**
	 * The generic name of the group, which is equal to all the modifiedName of
	 * the NameSimilarityResultName objects contained in the group.
	 */
	private String name;

	/**
	 * Individual names that form the group. All their modifiedName are
	 * identical and equal to the name of the group.
	 */
	private List<NameSimilarityResultName> resultNames;

	/**
	 * Creates a NameSimilarityResultGroup with the specified name.
	 * 
	 * @param name
	 *            Name of the group.
	 */
	public NameSimilarityResultGroup(String name) {
		this.name = name;
		resultNames = new LinkedList<NameSimilarityResultName>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NameSimilarityResultName> getResultNames() {
		return new LinkedList<NameSimilarityResultName>(resultNames);
	}

	public void setResultNames(List<NameSimilarityResultName> resultNames) {
		this.resultNames = new LinkedList<NameSimilarityResultName>(resultNames);
	}

	public void addName(NameSimilarityResultName name) {
		resultNames.add(name);
	}

	public void removeName(NameSimilarityResultName name) {
		resultNames.remove(name);
	}
}
