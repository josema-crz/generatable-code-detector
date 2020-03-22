/*
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package similaritycalculation.gst;

import java.util.Set;

/**
 * Represents a tile in the sense of the GST algorithm.
 * 
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class Tile {

	/**
	 * Separates the different elements of the tile in the triple
	 * representation.
	 */
	private static final String DELIM = ",";

	/** Start position of the tile in the source token list. */
	private int startElementA;

	/** Start position of the tile in the target token list. */
	private int startElementB;

	/** Length of the match in number of tokens. */
	private int tokenLength;

	/**
	 * Creates a new tile.
	 * 
	 * @param startElementA
	 *            Start position of the tile in the source token list.
	 * @param startElementB
	 *            Start position of the tile in the target token list.
	 * @param tokenLength
	 *            Length of the match.
	 */
	public Tile(final int startElementA, final int startElementB, final int tokenLength) {
		this.startElementA = startElementA;
		this.startElementB = startElementB;
		this.tokenLength = tokenLength;
	}

	/**
	 * Returns the length of the match.
	 * 
	 * @return Number of tokens of the match.
	 */
	public int getTokenLength() {
		return this.tokenLength;
	}

	/**
	 * Returns the start position of the tile in the source token list.
	 * 
	 * @return Start position of the tile in the source token list.
	 */
	public int getStartElementA() {
		return this.startElementA;
	}

	/**
	 * Returns the start position of the tile in the target token list.
	 * 
	 * @return Start position of the tile in the target token list.
	 */
	public int getStartElementB() {
		return this.startElementB;
	}

	/**
	 * Calculates the length of the matches of all tiles from a set of tiles.
	 * The length is measured in number of tokens.
	 * 
	 * @param tileSet
	 *            Set of tiles.
	 * @return Length of the matches of all tiles, in number of tokens.
	 */
	public static int getTokenLengthOfSetOfTiles(final Set<Tile> tileSet) {
		if (tileSet == null) {
			throw new IllegalArgumentException("The tile set can not be null");
		}

		int sum = 0;

		for (Tile tile : tileSet) {
			sum += tile.getTokenLength();
		}

		return sum;
	}

	/**
	 * Returns a string representing a tile in a triple notation: (start
	 * position source token list, start position target token list, match
	 * length).
	 */
	@Override
	public String toString() {
		final StringBuffer strBuf = new StringBuffer();
		strBuf.append("(");
		strBuf.append(this.startElementA);
		strBuf.append(Tile.DELIM);
		strBuf.append(this.startElementB);
		strBuf.append(Tile.DELIM);
		strBuf.append(this.tokenLength);
		strBuf.append(")");

		return strBuf.toString();
	}
}
