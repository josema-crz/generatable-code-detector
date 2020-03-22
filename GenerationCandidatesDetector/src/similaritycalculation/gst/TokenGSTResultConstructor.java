package similaritycalculation.gst;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import similaritycalculation.CodeUnit;
import similaritycalculation.SimilarCodePiecePair;
import similaritycalculation.SimilarityCalculationResult;

/**
 * Implementation of {@link IGSTResultConstructor} which uses the token as the
 * unit of measure for constructing the result.
 */
public class TokenGSTResultConstructor implements IGSTResultConstructor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimilarityCalculationResult getResult(CodeUnit codeUnitA, List<Token> tokenListA, CodeUnit codeUnitB,
			List<Token> tokenListB, Set<Tile> calculatedTiles) {
		// The result measures will all be expressed in number of tokens.

		// We get the total number of tokens found similar
		int similarTokens = Tile.getTokenLengthOfSetOfTiles(calculatedTiles);

		// We get the total number of tokens of each file
		int totalTokensA = tokenListA.size();
		int totalTokensB = tokenListB.size();

		// We find the biggest similar part
		int sizeBiggestTile = 0;
		Set<Tile> biggestTiles = new HashSet<Tile>();
		for (Tile curTile : calculatedTiles) {
			int curSize = curTile.getTokenLength();
			if (curSize > sizeBiggestTile) {
				sizeBiggestTile = curSize;
				biggestTiles.clear();
				biggestTiles.add(curTile);
			} else if (curSize == sizeBiggestTile) {
				biggestTiles.add(curTile);
			}
		}

		// We create and initialize the result
		SimilarityCalculationResult result = new SimilarityCalculationResult(codeUnitA, codeUnitB, similarTokens,
				totalTokensA, totalTokensB);
		result.setBiggestSimilarPartLength(sizeBiggestTile);
		result.setSimilarSourceCodePieces(
				getSimilarCodePiecesPairsFromTiles(codeUnitA, tokenListA, codeUnitB, tokenListB, calculatedTiles));

		return result;
	}

	/**
	 * Creates a list of {@link SimilarCodePiecePair} with the similar parts
	 * found so that they can be traced back to the source code.
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
	 * @return List of {@link SimilarCodePiecePair} calculated from the tiles.
	 */
	private List<SimilarCodePiecePair> getSimilarCodePiecesPairsFromTiles(CodeUnit codeUnitA, List<Token> tokenListA,
			CodeUnit codeUnitB, List<Token> tokenListB, Set<Tile> tiles) {
		List<SimilarCodePiecePair> pieces = new LinkedList<SimilarCodePiecePair>();

		for (Tile tile : tiles) {
			SimilarCodePiecePair pair = getSimilarCodePiecesPairFromTile(codeUnitA, tokenListA, codeUnitB, tokenListB,
					tile);
			if (pair != null) {
				pieces.add(pair);
			}
		}

		return pieces;
	}

	/**
	 * Searches in the information contained in the tile for the pair of code
	 * pieces found similar.
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
	 * @return A pair of code pieces with the positions in the source code unit.
	 */
	private SimilarCodePiecePair getSimilarCodePiecesPairFromTile(CodeUnit codeUnitA, List<Token> tokenListA,
			CodeUnit codeUnitB, List<Token> tokenListB, Tile tile) {
		// We search for the first pair of tokens defined in the tile with a
		// code piece associated.
		// Both tokens must have a code piece, since we want to pair up only
		// pieces
		// of code corresponding to the same tokens.
		for (int i = 0; i < tile.getTokenLength(); i++) {
			Token firstTokenA = tokenListA.get(tile.getStartElementA() + i);
			Token firstTokenB = tokenListB.get(tile.getStartElementB() + i);
			if (firstTokenA.getCodePiece() != null && firstTokenB.getCodePiece() != null) {
				int startPosA = firstTokenA.getCodePiece().getStartPosition();
				int startPosB = firstTokenB.getCodePiece().getStartPosition();

				// We search for the last pair of tokens defined in the tile
				// with a code piece associated.
				for (int j = tile.getTokenLength() - 1; j >= 0; j--) {
					Token lastTokenA = tokenListA.get(tile.getStartElementA() + j);
					Token lastTokenB = tokenListB.get(tile.getStartElementB() + j);

					if (lastTokenA.getCodePiece() != null && lastTokenB.getCodePiece() != null) {
						int endPosA = lastTokenA.getCodePiece().getEndPosition();
						int endPosB = lastTokenB.getCodePiece().getEndPosition();

						return new SimilarCodePiecePair(codeUnitA, startPosA, endPosA, codeUnitB, startPosB, endPosB);
					}
				}
				// The loop will never complete, since it will finish at the
				// latest when j=i
			}
		}
		return null;
	}
}
