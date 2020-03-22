package customclonedetection;

import java.util.LinkedList;
import java.util.List;

/**
 * Describes the result of calculating the similarity between two code units.
 */
public class SimilarityCalculationResult {
	public final static int NO_BIGGEST_PART_LENGTH_VALUE = -1;

	/** Code units whose similarity has been calculated. */
	private CodeUnit codeUnitA, codeUnitB;

	/**
	 * The amount of code that the two code units share. The measure unit is
	 * free and depends on the implementation. It can be the number of similar
	 * lines, tokens, characters, etc.
	 */
	private float similarity;

	/**
	 * The percentage of the code units found similar. This is a measure
	 * relative to the similarity obtained and the total length of the code
	 * units (independently of the measure unit used for those numbers).
	 */
	private float percentageCodeUnitA, percentageCodeUnitB;

	/**
	 * The list of pieces of code found equal in both code units, delimited by
	 * their start and end positions in the original code.
	 */
	private List<SimilarCodePiecePair> similarSourceCodePieces;

	/**
	 * The length of the biggest part of code found in both code units,
	 * according to the unit measure used for the previous attributes.
	 */
	private int biggestSimilarPartLength;

	/**
	 * Creates a new similarity result between two code units from the
	 * similarity measure and the length of the code units.
	 * 
	 * @param codeUnitA
	 *            The first code unit.
	 * @param codeUnitB
	 *            The second code unit.
	 * @param similarity
	 *            The amount of code that the two code units share.
	 * @param codeUnitALength
	 *            The length of the first code unit.
	 * @param codeUnitBLength
	 *            The length of the second code unit.
	 */
	public SimilarityCalculationResult(CodeUnit codeUnitA, CodeUnit codeUnitB, float similarity, int codeUnitALength,
			int codeUnitBLength) {
		this(codeUnitA, codeUnitB, similarity, (float) similarity / (float) codeUnitALength,
				(float) similarity / (float) codeUnitBLength);
	}

	/**
	 * Creates a new similarity result between two code units from the
	 * similarity measure and the percentages that the similar code represent in
	 * each code unit.
	 * 
	 * @param codeUnitA
	 *            The first code unit.
	 * @param codeUnitB
	 *            The second code unit.
	 * @param similarity
	 *            The amount of code that the two code units share.
	 * @param percentageCodeUnitA
	 *            The percentage of similar code in the first code unit.
	 * @param percentageCodeUnitB
	 *            The percentage of similar code in the second code unit.
	 */
	public SimilarityCalculationResult(CodeUnit codeUnitA, CodeUnit codeUnitB, float similarity,
			float percentageCodeUnitA, float percentageCodeUnitB) {
		this.codeUnitA = codeUnitA;
		this.codeUnitB = codeUnitB;
		this.similarity = similarity;
		this.percentageCodeUnitA = percentageCodeUnitA;
		this.percentageCodeUnitB = percentageCodeUnitB;

		// Default values
		this.similarSourceCodePieces = new LinkedList<SimilarCodePiecePair>();
		this.biggestSimilarPartLength = NO_BIGGEST_PART_LENGTH_VALUE;
	}

	public CodeUnit getCodeUnitA() {
		return codeUnitA;
	}

	public void setCodeUnitA(CodeUnit codeUnitA) {
		this.codeUnitA = codeUnitA;
	}

	public CodeUnit getCodeUnitB() {
		return codeUnitB;
	}

	public void setCodeUnitB(CodeUnit codeUnitB) {
		this.codeUnitB = codeUnitB;
	}

	public float getSimilarity() {
		return similarity;
	}

	public void setSimilarity(float similarity) {
		this.similarity = similarity;
	}

	public float getPercentageCodeUnitA() {
		return percentageCodeUnitA;
	}

	public void setPercentageCodeUnitA(float percentageCodeUnitA) {
		this.percentageCodeUnitA = percentageCodeUnitA;
	}

	public float getPercentageCodeUnitB() {
		return percentageCodeUnitB;
	}

	public void setPercentageCodeUnitB(float percentageCodeUnitB) {
		this.percentageCodeUnitB = percentageCodeUnitB;
	}

	public int getBiggestSimilarPartLength() {
		return biggestSimilarPartLength;
	}

	public void setBiggestSimilarPartLength(int biggestSimilarPartLength) {
		this.biggestSimilarPartLength = biggestSimilarPartLength;
	}

	public List<SimilarCodePiecePair> getSimilarSourceCodePieces() {
		return new LinkedList<SimilarCodePiecePair>(similarSourceCodePieces);
	}

	public void setSimilarSourceCodePieces(List<SimilarCodePiecePair> similarSourceCodePieces) {
		this.similarSourceCodePieces = new LinkedList<SimilarCodePiecePair>(similarSourceCodePieces);
	}
}
