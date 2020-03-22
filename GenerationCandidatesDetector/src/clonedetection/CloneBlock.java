package clonedetection;

import utils.FileUtils;

/**
 * Represents a clone block inside a clone set, that is, a specific portion of
 * code in a file that has been found equal to other blocks.
 */
public class CloneBlock {
	/** Name of the file */
	private String fileName;
	/** Full name of the file, including the path */
	private String fileFullName;
	/** Start and end line numbers of the clone block */
	private int startLineNumber, endLineNumber;

	/**
	 * Creates a clone block from the full name of the file and the start and
	 * end line numbers.
	 * 
	 * @param fileFullName
	 *            Full name of the file
	 * @param startLineNumber
	 *            Start line number of the code portion
	 * @param endLineNumber
	 *            End line number of the code portion
	 */
	public CloneBlock(String fileFullName, int startLineNumber, int endLineNumber) {
		this.fileFullName = fileFullName;
		// Get the simple name from the full name
		this.fileName = FileUtils.getSimpleName(fileFullName);
		this.startLineNumber = startLineNumber;
		this.endLineNumber = endLineNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileFullName() {
		return fileFullName;
	}

	public void setFileFullName(String fileFullName) {
		this.fileFullName = fileFullName;
	}

	public int getStartLineNumber() {
		return startLineNumber;
	}

	public void setStartLineNumber(int startLineNumber) {
		this.startLineNumber = startLineNumber;
	}

	public int getEndLineNumber() {
		return endLineNumber;
	}

	public void setEndLineNumber(int endLineNumber) {
		this.endLineNumber = endLineNumber;
	}

}
