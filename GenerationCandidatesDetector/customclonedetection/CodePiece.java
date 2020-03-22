package customclonedetection;

/**
 * Represents a piece of code in a code unit, defined by the start and end
 * positions.
 */
public class CodePiece {
	/** Code unit. */
	private CodeUnit codeUnit;

	/** Start position. */
	private int startPosition;

	/** End position. */
	private int endPosition;

	/**
	 * Creates a code piece.
	 * 
	 * @param codeUnit
	 *            The code unit.
	 * @param startPosition
	 *            The start position.
	 * @param endPosition
	 *            The end position.
	 */
	public CodePiece(CodeUnit codeUnit, int startPosition, int endPosition) {
		this.codeUnit = codeUnit;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

	public CodeUnit getCodeUnit() {
		return codeUnit;
	}

	public void setCodeUnit(CodeUnit codeUnit) {
		this.codeUnit = codeUnit;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}
}
