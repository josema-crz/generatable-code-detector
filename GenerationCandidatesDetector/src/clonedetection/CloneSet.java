package clonedetection;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a set of clones, that is, a group of files that share a duplicated
 * portion of code.
 */
public class CloneSet {
	/** List of clone blocks in the set */
	private List<CloneBlock> blocks;
	/** Number of lines of each and every clone block in the set */
	private int lineCount;
	/** Actual portion of code that is shared between the clones */
	private String duplicatedCode;
	/** Similarity measure between the clones in the set */
	private double similarity;

	/**
	 * Initializes an empty CloneSet.
	 */
	public CloneSet() {
		this.blocks = new LinkedList<CloneBlock>();

		// Default values for the rest of fields
		this.lineCount = -1;
		this.duplicatedCode = null;
		this.similarity = -1;
	}

	/**
	 * Returns the size of the set.
	 * 
	 * @return The size of the set, which is the amount of blocks it contains
	 */
	public int getSize() {
		return blocks.size();
	}

	public List<CloneBlock> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<CloneBlock> blocks) {
		this.blocks = blocks;
	}
	
	public void addBlock(CloneBlock block) {
		blocks.add(block);
	}

	public int getLineCount() {
		return lineCount;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	public String getDuplicatedCode() {
		return duplicatedCode;
	}

	public void setDuplicatedCode(String duplicatedCode) {
		this.duplicatedCode = duplicatedCode;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
}
