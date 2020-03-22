package similaritycalculation;

import java.util.LinkedList;
import java.util.List;

import generationcandidatesdetector.GenerationCandidatesDetectorResult;

/**
 * Represents the result of applying the similarity calculation process to more
 * than two code units.
 */
public class MultipleGlobalSimilarityCalculationResult implements GenerationCandidatesDetectorResult {
	private List<GlobalSimilarityCalculationResult> results;

	public MultipleGlobalSimilarityCalculationResult(List<GlobalSimilarityCalculationResult> results) {
		super();
		this.results = results;
	}

	/**
	 * Returns a measure of the overall similarity of the elements compared,
	 * calculated from the individual final similarity results.
	 * 
	 * @return The total similarity measure as explained.
	 */
	public float getTotalSimilarity() {
		// We just calculate and return the average.
		float sumSim = 0;
		for (GlobalSimilarityCalculationResult result : results) {
			sumSim += result.getFinalResult().getSimilarity();
		}
		if (sumSim != 0) {
			return sumSim / results.size();
		}
		return sumSim;
	}

	public MultipleGlobalSimilarityCalculationResult() {
		super();
		this.results = new LinkedList<GlobalSimilarityCalculationResult>();
	}

	public List<GlobalSimilarityCalculationResult> getResults() {
		return results;
	}

	public void setResults(List<GlobalSimilarityCalculationResult> results) {
		this.results = results;
	}

	public void addResult(GlobalSimilarityCalculationResult result) {
		results.add(result);
	}
}
