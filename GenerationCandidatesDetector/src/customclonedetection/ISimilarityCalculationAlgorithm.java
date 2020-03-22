package similaritycalculation;

import org.eclipse.jdt.core.IType;

/**
 * This interface has to be implemented by algorithms that calculate the
 * similarity of two code units for one specific step in the global similarity
 * calculation process.
 */
public interface ISimilarityCalculationAlgorithm {
	/**
	 * Calculates the similarity between two code units for one specific step in
	 * the global similarity calculation process.
	 * 
	 * @param codeUnitA
	 *            First code unit.
	 * @param codeUnitB
	 *            Second code unit.
	 * @param modelClassA
	 *            Model class related to the first code unit. It can be null,
	 *            but some pre-processing actions will not be effective.
	 * @param modelClassB
	 *            Model class related to the second code unit. It can be null,
	 *            but some pre-processing actions will not be effective.
	 * @param step
	 *            Step in the global process.
	 * @return A result with the similarity measures.
	 * @throws SimilarityCalculationException
	 */
	SimilarityCalculationResult calculateSimilarity(CodeUnit codeUnitA, CodeUnit codeUnitB, IType modelClassA,
			IType modelClassB, int step) throws SimilarityCalculationException;
}
