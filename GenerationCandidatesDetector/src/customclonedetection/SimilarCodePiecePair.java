package customclonedetection;

/**
 * Represents two pieces of code that have been found similar.
 */
public class SimilarCodePiecePair {
	/**
	 * Piece of code in code unit A.
	 */
	private CodePiece pieceA;

	/**
	 * Piece of code in code unit B.
	 */
	private CodePiece pieceB;

	/**
	 * Creates a code piece pair.
	 * 
	 * @param codeUnitA
	 *            First code unit.
	 * @param startPositionA
	 *            Start position of the code piece in the first code unit.
	 * @param endPositionA
	 *            End position of the code piece in the first code unit.
	 * @param codeUnitB
	 *            Second code unit.
	 * @param startPositionB
	 *            Start position of the code piece in the second code unit.
	 * @param endPositionB
	 *            End position of the code piece in the second code unit.
	 */
	public SimilarCodePiecePair(CodeUnit codeUnitA, int startPositionA, int endPositionA, CodeUnit codeUnitB,
			int startPositionB, int endPositionB) {
		this.pieceA = new CodePiece(codeUnitA, startPositionA, endPositionA);
		this.pieceB = new CodePiece(codeUnitB, startPositionB, endPositionB);
	}

	public CodePiece getPieceA() {
		return pieceA;
	}

	public void setPieceA(CodePiece pieceA) {
		this.pieceA = pieceA;
	}

	public CodePiece getPieceB() {
		return pieceB;
	}

	public void setPieceB(CodePiece pieceB) {
		this.pieceB = pieceB;
	}

}
