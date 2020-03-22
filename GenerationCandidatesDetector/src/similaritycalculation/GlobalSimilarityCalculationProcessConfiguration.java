package similaritycalculation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import similaritycalculation.gst.GSTSimilarityCalculationAlgorithm;

/**
 * Contains some configuration values for the global similarity calculation
 * process.
 */
public class GlobalSimilarityCalculationProcessConfiguration {
	/** Algorithm used to obtain each individual similarity measure. */
	private ISimilarityCalculationAlgorithm algorithm;

	/** Steps that will be applied during the similarity calculation process. */
	private List<Integer> steps;

	/**
	 * Weights assigned to each applied step when calculating the final measures
	 * from the step results. Each weight should be a value between 0 and 1, and
	 * the sum of all the weights should be 1.
	 */
	private List<Double> stepWeights;

	/**
	 * Creates a configuration for a global similarity calculation process.
	 * 
	 * @param algorithm
	 *            Algorithm used to obtain each individual similarity measure.
	 * @param steps
	 *            Steps that will be applied during the similarity calculation
	 *            process.
	 * @param stepWeights
	 *            Weights assigned to each applied step when calculating the
	 *            final measures from the step results. There must be a weight
	 *            for each step specified in the {@code steps} parameter. Each
	 *            weight should be a value between 0 and 1, and the sum of all
	 *            the weights should be 1.
	 */
	public GlobalSimilarityCalculationProcessConfiguration(ISimilarityCalculationAlgorithm algorithm,
			List<Integer> steps, List<Double> stepWeights) {
		if (steps.size() > stepWeights.size()) {
			throw new IllegalArgumentException("A weight must be specified for each step.");
		}

		this.algorithm = algorithm;
		this.steps = new LinkedList<Integer>(steps);
		this.stepWeights = new LinkedList<Double>(stepWeights);
	}

	/**
	 * Returns a configuration with some default values for the attributes:
	 * <ul>
	 * <li>The GST algorithm is used, with a default configuration.</li>
	 * <li>The four pre-processing steps will be executed.</li>
	 * <li>The weights assigned to the step are: 0.3 - 0.4 - 0.15 - 0.15
	 * </li>
	 * </ul>
	 * 
	 * @return The default configuration.
	 */
	public static GlobalSimilarityCalculationProcessConfiguration getDefaultConfiguration() {
		ISimilarityCalculationAlgorithm algorithm = new GSTSimilarityCalculationAlgorithm();
		List<Integer> steps = Arrays.asList(1, 2, 3, 4);
		List<Double> stepWeights = Arrays.asList(0.3, 0.4, 0.15, 0.15);

		return new GlobalSimilarityCalculationProcessConfiguration(algorithm, steps, stepWeights);
	}

	public ISimilarityCalculationAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(ISimilarityCalculationAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public List<Integer> getSteps() {
		return steps;
	}

	public void setSteps(List<Integer> steps) {
		this.steps = steps;
	}

	public List<Double> getStepWeights() {
		return stepWeights;
	}

	public void setStepWeights(List<Double> stepWeights) {
		this.stepWeights = stepWeights;
	}
}
