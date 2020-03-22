package similaritycalculation;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.core.IType;

/**
 * Represents the result of the global similarity calculation process between
 * two code units. A GlobalResult contains additional results calculated during
 * the steps of the process.
 */
public class GlobalSimilarityCalculationResult {
	/** Code units whose similarity has been calculated. */
	private CodeUnit codeUnitA, codeUnitB;

	/** Model classes related to the code units. */
	private IType modelClassA, modelClassB;

	/** Individual results for each step of the process. */
	private Map<Integer, SimilarityCalculationResult> stepResults;

	/**
	 * Final result calculated from the individual results.
	 */
	private SimilarityCalculationResult finalResult;

	/**
	 * Creates a global similarity result.
	 * 
	 * @param codeUnitA
	 *            First code unit.
	 * @param codeUnitB
	 *            Second code unit.
	 * @param modelClassA
	 *            Model class related to code unit A.
	 * @param modelClassB
	 *            Model class related to code unit B.
	 */
	public GlobalSimilarityCalculationResult(CodeUnit codeUnitA, CodeUnit codeUnitB, IType modelClassA,
			IType modelClassB) {
		this.codeUnitA = codeUnitA;
		this.codeUnitB = codeUnitB;
		this.modelClassA = modelClassA;
		this.modelClassB = modelClassB;
		this.stepResults = new TreeMap<Integer, SimilarityCalculationResult>();
	}

	/**
	 * Creates the similarity result with the final measures calculated from the
	 * step results.
	 * 
	 * @param stepWeights
	 *            Weights assigned to the similarity measures of each step
	 *            result.
	 * @throws SimilarityCalculationException
	 *             When there is a step result missing.
	 */
	public void calculateFinalResult(List<Integer> steps, List<Double> stepWeights)
			throws SimilarityCalculationException {
		if (steps.size() > stepWeights.size()) {
			throw new IllegalArgumentException(
					"A weight must be specified for each step when calculating the final result.");
		}

		// Calculate the total measures from the weighted step measures.
		float totalSimilarity = 0;
		float totalPercentageCodeUnitA = 0;
		float totalPercentageCodeUnitB = 0;
		// For each step, we get the corresponding result and its weight and
		// update the totals.
		for (int i = 0; i < steps.size(); i++) {
			SimilarityCalculationResult result = stepResults.get(steps.get(i));

			if (result == null) {
				throw new SimilarityCalculationException("The result for the step " + steps.get(i) + " is missing.");
			}

			totalSimilarity += result.getSimilarity() * stepWeights.get(i);
			totalPercentageCodeUnitA += result.getPercentageCodeUnitA() * stepWeights.get(i);
			totalPercentageCodeUnitB += result.getPercentageCodeUnitB() * stepWeights.get(i);
		}

		// Find the biggest similar part among the step results.
		// (For better efficiency, this calculation could be integrated in the
		// loop above)
		int biggestSimilarPartLength = SimilarityCalculationResult.NO_BIGGEST_PART_LENGTH_VALUE;
		for (SimilarityCalculationResult result : stepResults.values()) {
			if (result.getBiggestSimilarPartLength() > biggestSimilarPartLength) {
				biggestSimilarPartLength = result.getBiggestSimilarPartLength();
			}
		}

		// Create and initialize the final result
		finalResult = new SimilarityCalculationResult(codeUnitA, codeUnitB, totalSimilarity, totalPercentageCodeUnitA,
				totalPercentageCodeUnitB);
		finalResult.setBiggestSimilarPartLength(biggestSimilarPartLength);
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

	/**
	 * Adds the result of a specific step to the global result.
	 * 
	 * @param step
	 *            Number of the step.
	 * @param stepResult
	 *            Result of the step.
	 */
	public void addStepResult(int step, SimilarityCalculationResult stepResult) {
		stepResults.put(step, stepResult);
	}

	/**
	 * Removes the result of a specific step from the global result.
	 * 
	 * @param step
	 *            Number of the step.
	 */
	public void removeStepResult(int step) {
		stepResults.remove(step);
	}

	/**
	 * Gets the result of a specific step from the global result.
	 * 
	 * @param step
	 *            Number of the step.
	 */
	public SimilarityCalculationResult getStepResult(int step) {
		return stepResults.get(step);
	}

	public SimilarityCalculationResult getFinalResult() {
		return finalResult;
	}

	public void setFinalResult(SimilarityCalculationResult finalResult) {
		this.finalResult = finalResult;
	}

	public Map<Integer, SimilarityCalculationResult> getStepResults() {
		return stepResults;
	}

	/**
	 * Returns a string with a summary of the result.
	 * 
	 * @return The summary.
	 */
	public String getSummary() {
		StringBuilder summary = new StringBuilder();

		// Print the final results.
		summary.append("Similarity between <" + getCodeUnitA().getName() + "> and <" + getCodeUnitB().getName() + ">.");
		summary.append("\n");
		summary.append("The global similarity measure between the two files is " + getFinalResult().getSimilarity());
		summary.append("\n");
		summary.append("The global percentages of similar code in both files are respectively "
				+ getFinalResult().getPercentageCodeUnitA() * 100 + "% and "
				+ getFinalResult().getPercentageCodeUnitB() * 100 + "%.");
		summary.append("\n");
		summary.append(
				"The biggest similar part found has a length of " + getFinalResult().getBiggestSimilarPartLength());
		summary.append("\n");
		summary.append("\n");

		// Print the result for each step.
		float prevSimilarity = -1;
		float prevPercentageA = -1;
		float prevPercentageB = -1;
		for (Map.Entry<Integer, SimilarityCalculationResult> entry : getStepResults().entrySet()) {
			SimilarityCalculationResult stepResult = entry.getValue();
			summary.append("Step " + entry.getKey() + " results.");
			summary.append("\n");
			summary.append("The similarity measure is " + stepResult.getSimilarity());
			summary.append("\n");
			summary.append("The percentages of similar code in both files are respectively "
					+ stepResult.getPercentageCodeUnitA() * 100 + "% and " + stepResult.getPercentageCodeUnitB() * 100
					+ "%.");
			summary.append("\n");

			if (prevSimilarity != -1) {
				float incSimilarity = prevSimilarity == 0 ? 0 : stepResult.getSimilarity() / prevSimilarity;
				float incPercentageA = prevPercentageA == 0 ? 0 : stepResult.getPercentageCodeUnitA() / prevPercentageA;
				float incPercentageB = prevPercentageB == 0 ? 0 : stepResult.getPercentageCodeUnitB() / prevPercentageB;

				summary.append("These measures relative to the previous step are " + incSimilarity + " ("
						+ incPercentageA + "/" + incPercentageB + ")");
				summary.append("\n");
				summary.append("According to: similarity (percentage in code unit A / percentage in code unit B)");
				summary.append("\n");
			}

			summary.append(
					"The biggest similar part found has a length of " + stepResult.getBiggestSimilarPartLength());
			summary.append("\n");
			summary.append("\n");

			prevSimilarity = stepResult.getSimilarity();
			prevPercentageA = stepResult.getPercentageCodeUnitA();
			prevPercentageB = stepResult.getPercentageCodeUnitB();
		}

		return summary.toString();
	}

	public IType getModelClassA() {
		return modelClassA;
	}

	public void setModelClassA(IType modelClassA) {
		this.modelClassA = modelClassA;
	}

	public IType getModelClassB() {
		return modelClassB;
	}

	public void setModelClassB(IType modelClassB) {
		this.modelClassB = modelClassB;
	}

}
