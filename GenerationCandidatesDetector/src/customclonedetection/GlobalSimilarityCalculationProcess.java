package similaritycalculation;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.IType;

/**
 * Implements the global similarity calculation process, where the similarity
 * between two code units is obtained through a process usually formed by
 * several steps. A global similarity measure is returned, containing the
 * additional individual measures calculated during the process.
 */
public class GlobalSimilarityCalculationProcess implements IGlobalSimilarityCalculationProcess {
	/** Configuration values */
	private GlobalSimilarityCalculationProcessConfiguration configuration;

	/**
	 * Creates a similarity calculation process with the specified
	 * configuration.
	 * 
	 * @param configuration
	 *            Configuration values for the process.
	 */
	public GlobalSimilarityCalculationProcess(GlobalSimilarityCalculationProcessConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Creates a similarity calculation process with a default configuration.
	 */
	public GlobalSimilarityCalculationProcess() {
		this(GlobalSimilarityCalculationProcessConfiguration.getDefaultConfiguration());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GlobalSimilarityCalculationResult calculateSimilarity(CodeUnit codeUnitA, CodeUnit codeUnitB,
			IType modelClassA, IType modelClassB) throws SimilarityCalculationException {
		// We create the global result for these two code units.
		GlobalSimilarityCalculationResult result = new GlobalSimilarityCalculationResult(codeUnitA, codeUnitB,
				modelClassA, modelClassB);

		// We obtain the individual similarity result for each step using the
		// algorithm, and include each result in the global result.
		for (int step : configuration.getSteps()) {
			result.addStepResult(step, configuration.getAlgorithm().calculateSimilarity(codeUnitA, codeUnitB,
					modelClassA, modelClassB, step));
		}

		// We calculate the final measures from the individual results.
		result.calculateFinalResult(configuration.getSteps(), configuration.getStepWeights());

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GlobalSimilarityCalculationResult calculateSimilarity(CodeUnit codeUnitA, CodeUnit codeUnitB)
			throws SimilarityCalculationException {
		return this.calculateSimilarity(codeUnitA, codeUnitB, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MultipleGlobalSimilarityCalculationResult calculateSimilarity(CodeUnit codeUnitA, IType modelClassA,
			Map<CodeUnit, IType> otherCodeUnits) throws SimilarityCalculationException {
		MultipleGlobalSimilarityCalculationResult result = new MultipleGlobalSimilarityCalculationResult();

		for (Entry<CodeUnit, IType> entry : otherCodeUnits.entrySet()) {
			// Calculate the similarity and store the result.
			result.addResult(calculateSimilarity(codeUnitA, entry.getKey(), modelClassA, entry.getValue()));
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MultipleGlobalSimilarityCalculationResult calculateSimilarity(Map<CodeUnit, IType> codeUnits)
			throws SimilarityCalculationException {
		MultipleGlobalSimilarityCalculationResult result = new MultipleGlobalSimilarityCalculationResult();

		// Iterate through all the code units in the map.
		for (Map.Entry<CodeUnit, IType> entry1 : codeUnits.entrySet()) {
			CodeUnit key1 = entry1.getKey();
			int hash1 = System.identityHashCode(key1);
			// Iterate again through the map.
			for (Map.Entry<CodeUnit, IType> entry2 : codeUnits.entrySet()) {
				CodeUnit key2 = entry2.getKey();
				// Only consider the code units "after" the current one, to
				// avoid duplicated pairs.
				// We use the identity hash code for the comparison.
				if (hash1 >= System.identityHashCode(key2))
					continue;

				// Calculate the similarity between these two code units and
				// store the result.
				result.addResult(
						calculateSimilarity(entry1.getKey(), entry2.getKey(), entry1.getValue(), entry2.getValue()));
			}
		}

		return result;
	}
}
