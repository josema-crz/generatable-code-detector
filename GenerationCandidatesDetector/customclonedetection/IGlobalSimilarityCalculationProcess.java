package customclonedetection;

import java.util.Map;

import org.eclipse.jdt.core.IType;

/**
 * Interface implemented by any class that implements a complex similarity
 * calculation process to obtain a similarity measure between two code units.
 */
public interface IGlobalSimilarityCalculationProcess {
	/**
	 * Calculates the global similarity between two code units.
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
	 * @return A global result, containing additional results calculated during
	 *         the process.
	 * @throws SimilarityCalculationException
	 */
	public GlobalSimilarityCalculationResult calculateSimilarity(CodeUnit codeUnitA, CodeUnit codeUnitB,
			IType modelClassA, IType modelClassB) throws SimilarityCalculationException;

	/**
	 * Calculates the global similarity between two code units. The model
	 * classes related to the code units are not specified, and therefore
	 * generation-specific pre-processing actions will not be applied.
	 * 
	 * @param codeUnitA
	 *            First code unit.
	 * @param codeUnitB
	 *            Second code unit.
	 * @return A global result, containing additional results calculated during
	 *         the process.
	 * @throws SimilarityCalculationException
	 */
	public GlobalSimilarityCalculationResult calculateSimilarity(CodeUnit codeUnitA, CodeUnit codeUnitB)
			throws SimilarityCalculationException;

	/**
	 * Calculates the global similarity between a code unit and a list of other
	 * code units.
	 * 
	 * @param codeUnitA
	 *            Code unit to be compared to the ones in the list.
	 * @param modelClassA
	 *            Model class related to codeUnitA. It can be null, but some
	 *            pre-processing actions will not be effective.
	 * @param otherCodeUnits
	 *            List of code units that will be compared to the main code
	 *            unit. For each code unit, a related model class can be
	 *            specified, or null otherwise.
	 * @return The global results with the similarity between codeUnitA and each
	 *         code unit in otherCodeUnits.
	 * @throws SimilarityCalculationException
	 */
	public MultipleGlobalSimilarityCalculationResult calculateSimilarity(CodeUnit codeUnitA, IType modelClassA,
			Map<CodeUnit, IType> otherCodeUnits) throws SimilarityCalculationException;

	/**
	 * Calculates the global similarity between a list of code units, that is,
	 * between each possible pair of code units in that list.
	 * 
	 * @param codeUnits
	 *            Code units to be compared. For each code unit, a related model
	 *            class can be specified, or null otherwise.
	 * @return The global results with the similarity between the code units.
	 * @throws SimilarityCalculationException
	 */
	public MultipleGlobalSimilarityCalculationResult calculateSimilarity(Map<CodeUnit, IType> codeUnits)
			throws SimilarityCalculationException;
}
