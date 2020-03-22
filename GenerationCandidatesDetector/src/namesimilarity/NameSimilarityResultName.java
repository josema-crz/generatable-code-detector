package namesimilarity;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Name in the name similarity process, with the information about
 * the original string and the replacements made.
 */
public class NameSimilarityResultName {
	/** Original, full name of the file. */
	private String originalName;

	/** Name of the file after the replacements have been done. */
	private String modifiedName;

	/** Zero or more names from which the original name is derived. */
	private List<String> derivedFrom;

	/**
	 * Creates a NameSimilarityResultName from its original name.
	 * 
	 * @param originalName
	 *            Original name.
	 */
	public NameSimilarityResultName(String originalName) {
		this.originalName = originalName;
		this.modifiedName = originalName;
		this.derivedFrom = new LinkedList<String>();
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public List<String> getDerivedFrom() {
		return derivedFrom;
	}

	public void addDerivedFrom(String derivedFrom) {
		this.derivedFrom.add(derivedFrom);
	}

	public String getModifiedName() {
		return modifiedName;
	}

	public void setModifiedName(String modifiedName) {
		this.modifiedName = modifiedName;
	}
}
