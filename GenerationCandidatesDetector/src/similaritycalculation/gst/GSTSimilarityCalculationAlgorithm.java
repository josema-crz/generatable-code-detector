/*
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package similaritycalculation.gst;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IType;

import similaritycalculation.CodeUnit;
import similaritycalculation.ISimilarityCalculationAlgorithm;
import similaritycalculation.SimilarityCalculationException;
import similaritycalculation.SimilarityCalculationResult;
import utils.Logger;

/**
 * Implementation of the Greedy String Tiling algorithm used for calculating the
 * similarity of two code units represented through token lists.
 */
public class GSTSimilarityCalculationAlgorithm implements ISimilarityCalculationAlgorithm {
	/** Configuration values for the algorithm. */
	private GSTSimilarityCalculationAlgorithmConfiguration configuration;

	/**
	 * Set of calculated tiles resulting from the application of the algorithm.
	 */
	private Set<Tile> calculatedTiles;

	/** Token list for the first code unit. */
	private List<Token> codeUnitATokenList;

	/** Token list for the second code unit. */
	private List<Token> codeUnitBTokenList;

	/**
	 * Creates a GST algorithm with the specified configuration.
	 * 
	 * @param configuration
	 *            Configuration values.
	 */
	public GSTSimilarityCalculationAlgorithm(GSTSimilarityCalculationAlgorithmConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Creates a GST algorithm with the default configuration.
	 */
	public GSTSimilarityCalculationAlgorithm() {
		this(GSTSimilarityCalculationAlgorithmConfiguration.getDefaultConfiguration());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimilarityCalculationResult calculateSimilarity(CodeUnit codeUnitA, CodeUnit codeUnitB, IType modelClassA,
			IType modelClassB, int step) throws SimilarityCalculationException {
		// TODO Use of a Manager in between so we can reuse already created
		// token lists.
		try {
			codeUnitATokenList = configuration.getTokenizer().getTokenList(codeUnitA, modelClassA, step);
			codeUnitBTokenList = configuration.getTokenizer().getTokenList(codeUnitB, modelClassB, step);
		} catch (TokenizeException e) {
			throw new SimilarityCalculationException(e.getMessage());
		}

		// For debugging purposes
		//printTokenLists(step);

		calculatedTiles = compareTwoTokenlists(codeUnitATokenList, codeUnitBTokenList);

		SimilarityCalculationResult result = configuration.getResultConstructor().getResult(codeUnitA,
				codeUnitATokenList, codeUnitB, codeUnitBTokenList, calculatedTiles);

		return result;
	}

	/**
	 * Prints the content of the token lists of each file. Used for debugging
	 * purposes.
	 */
	private void printTokenLists(int step) {
		Logger.log("------ STEP " + step + " ------");
		Logger.log("------ Source 1 ------");
		for (Token token : codeUnitATokenList) {
			Logger.log(token.getValue().toString());
		}
		Logger.log("");
		Logger.log("------ Source 2 ------");
		for (Token token : codeUnitBTokenList) {
			Logger.log(token.getValue().toString());
		}
	}

	/**
	 * Compares the two given tokenlists and returns a set of tiles. This set of
	 * tiles denotes which ranges of the tokenlists are structurally equal. As a
	 * consequence of this, the biggest tile represents the biggest equal part
	 * etc.
	 * 
	 * @param tokenListA
	 *            First tokenlist.
	 * @param tokenListB
	 *            Second tokenlist.
	 * @return Set of tiles that shows which ranges of the tokenlists are
	 *         structurally equal.
	 */
	private Set<Tile> compareTwoTokenlists(List<Token> tokenListA, List<Token> tokenListB) {

		/* Used to store the tiles to be returned. */
		final Set<Tile> tiles = new HashSet<Tile>();

		int maxmatch = configuration.getMinimumMatchLength();

		final int greedyALength = tokenListA.size();
		final int greedyBLength = tokenListB.size();

		/*
		 * Both arrays indicate whether a particular position in the two
		 * tokenlists was already covered by a tile in a previous iteration of
		 * the GST algorithm. At the beginning all positions are unmarked as no
		 * tile was created. These arrays are used to prevent the algorithm from
		 * returning overlapping tiles.
		 */
		final boolean[] greedyAUnmarked = new boolean[greedyALength];
		final boolean[] greedyBUnmarked = new boolean[greedyBLength];

		for (int i = 0; i < greedyALength; i++) {
			greedyAUnmarked[i] = true;
		}

		for (int i = 0; i < greedyBLength; i++) {
			greedyBUnmarked[i] = true;
		}

		/* Stores all maximal matches found in the current iteration. */
		Set<Tile> matches = new HashSet<Tile>();

		do {
			maxmatch = configuration.getMinimumMatchLength();
			matches.clear();

			for (int unmarkedA = 0; unmarkedA < greedyALength; unmarkedA++) {
				for (int unmarkedB = 0; unmarkedB < greedyBLength; unmarkedB++) {
					int j = 0;

					while (unmarkedA + j < greedyALength && unmarkedB + j < greedyBLength
							&& greedyAUnmarked[unmarkedA + j] && greedyBUnmarked[unmarkedB + j]
							// Token value comparison
							&& tokenListA.get(unmarkedA + j).getValue().equals(tokenListB.get(unmarkedB + j).getValue())
							// String value comparison when both token values
							// are CUSTOM_STRING
							&& (tokenListA.get(unmarkedA + j).getValue() != TokenValue.CUSTOM_STRING
									|| tokenListA.get(unmarkedA + j).getExtraValue()
											.equals(tokenListB.get(unmarkedB + j).getExtraValue()))) {
						j++;
					}

					if (j == maxmatch) {
						matches.add(new Tile(unmarkedA, unmarkedB, j));
					} else if (j > maxmatch) {
						matches.clear();
						matches.add(new Tile(unmarkedA, unmarkedB, j));
						maxmatch = j;
					}
				}
			}

			markStringsAtTilePositions(matches, greedyAUnmarked, greedyBUnmarked, tiles);
		} while (maxmatch > configuration.getMinimumMatchLength());

		return tiles;
	}

	/**
	 * For all elements of the given set of tiles, the corresponding positions
	 * in the given boolean arrays are marked (set to false), provided that the
	 * tile does not overlap with an existing entry (an already marked
	 * position).
	 * 
	 * @param matches
	 *            Set of maximal matches, that were found in the current
	 *            iteration of the GST algorithm.
	 * @param greedyAUnmarked
	 *            Array which has the value <code>true</code> at one particular
	 *            position, if ElementA is unmarked at that position,
	 *            <code>false</code> otherwise.
	 * @param greedyBUnmarked
	 *            Array which has the value <code>true</code> at one particular
	 *            position, if ElementB is unmarked at that position,
	 *            <code>false</code> otherwise.
	 * @param tiles
	 *            The set of tiles to which new tiles are added, provided that
	 *            these tiles do not overlap with existing entries.
	 */
	private void markStringsAtTilePositions(final Set<Tile> matches, final boolean[] greedyAUnmarked,
			final boolean[] greedyBUnmarked, final Set<Tile> tiles) {
		for (Tile tileEntry : matches) {
			if (maximalMatchDoesNotOverlapWithExistingEntries(tileEntry, greedyAUnmarked, greedyBUnmarked)) {
				for (int j = 0; j < tileEntry.getTokenLength(); j++) {
					greedyAUnmarked[tileEntry.getStartElementA() + j] = false;
					greedyBUnmarked[tileEntry.getStartElementB() + j] = false;
				}

				tiles.add(tileEntry);
			}
		}
	}

	/**
	 * Checks for a maximal match whether it overlaps with existing entries.
	 * 
	 * @param maximalMatchToCheck
	 *            The maximal match for which it is checked whether it overlaps
	 *            with existing entries.
	 * @param greedyAUnmarked
	 *            Array which has the value <code>true</code> at one particular
	 *            position, if ElementA is unmarked at that position,
	 *            <code>false</code> otherwise.
	 * @param greedyBUnmarked
	 *            Array which has the value <code>true</code> at one particular
	 *            position, if ElementB is unmarked at that position,
	 *            <code>false</code> otherwise.
	 * @return <code>true</code> if the maximal match does not overlap with an
	 *         existing entry, <code>false</code> otherwise.
	 */
	private boolean maximalMatchDoesNotOverlapWithExistingEntries(final Tile maximalMatchToCheck,
			final boolean[] greedyAUnmarked, final boolean[] greedyBUnmarked) {
		final int endOfPatternTile = maximalMatchToCheck.getStartElementA() + maximalMatchToCheck.getTokenLength();

		final int endOfStringTile = maximalMatchToCheck.getStartElementB() + maximalMatchToCheck.getTokenLength();

		return greedyAUnmarked[endOfPatternTile - 1] && greedyAUnmarked[maximalMatchToCheck.getStartElementA()]
				&& greedyBUnmarked[maximalMatchToCheck.getStartElementB()] && greedyBUnmarked[endOfStringTile - 1];
	}

	public GSTSimilarityCalculationAlgorithmConfiguration getConfiguration() {
		return configuration;
	}
}
