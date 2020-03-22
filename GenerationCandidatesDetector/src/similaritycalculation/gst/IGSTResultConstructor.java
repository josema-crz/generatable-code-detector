package similaritycalculation.gst;

import java.util.List;
import java.util.Set;

import similaritycalculation.CodeUnit;
import similaritycalculation.SimilarityCalculationResult;

/**
 * This interface will be implemented by classes that build a
 * SimilarityCalculationResult object from the collections of tokens and tiles
 * resulting from the GST similarity calculation algorithm.
 */
public interface IGSTResultConstructor {
	/**
	 * Create a SimilarityCalculationResult object from the collections of
	 * tokens and tiles resulting from the GST similarity calculation algorithm.
	 * 
	 * @param codeUnitA
	 *            First code unit.
	 * @param tokenListA
	 *            Token list for the first code unit.
	 * @param codeUnitB
	 *            Second code unit.
	 * @param tokenListB
	 *            Token list for the second code unit.
	 * @param calculatedTiles
	 *            Set of calculated tiles with the similar parts.
	 * @return The similarity result.
	 */
	public SimilarityCalculationResult getResult(CodeUnit codeUnitA, List<Token> tokenListA, CodeUnit codeUnitB,
			List<Token> tokenListB, Set<Tile> calculatedTiles);
}
