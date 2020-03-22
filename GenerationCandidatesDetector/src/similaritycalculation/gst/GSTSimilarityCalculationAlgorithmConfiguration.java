package similaritycalculation.gst;

import similaritycalculation.gst.asttokenizer.EclipseASTTokenizer;

/**
 * Contains some configuration values for the GST similarity calculation
 * algorithm.
 */
public class GSTSimilarityCalculationAlgorithmConfiguration {
	/** Tokenizer used to obtain the list of tokens from the code unit. */
	private Tokenizer tokenizer;

	/**
	 * Entity in charge of creating the SimilarityCalculationResult from the
	 * resulting list of tiles.
	 */
	private IGSTResultConstructor resultConstructor;

	/**
	 * Contains the minimum match length (MML), which is used for the GST
	 * algorithm. If the algorithm calculates the similarity of two tokenlists,
	 * equal parts in both tokenlists must have at least this length. Let's
	 * assume the MML is set to 4. In this case, a part in two tokenlists is
	 * only considered as corresponding, if the part comprises at least 4
	 * tokens.
	 */
	private int minimumMatchLength;

	/**
	 * Creates a GST configuration with the information specified.
	 * 
	 * @param tokenizer
	 *            Tokenizer used to obtain the list of tokens from the code
	 *            unit.
	 * @param resultConstructor
	 *            Entity in charge of creating the SimilarityCalculationResult
	 *            from the resulting list of tiles.
	 * @param minimumMatchLength
	 *            The minimum token length of a similar part.
	 */
	public GSTSimilarityCalculationAlgorithmConfiguration(Tokenizer tokenizer, IGSTResultConstructor resultConstructor,
			int minimumMatchLength) {
		this.tokenizer = tokenizer;
		this.resultConstructor = resultConstructor;
		this.minimumMatchLength = minimumMatchLength;
	}

	/**
	 * Creates a GST configuration with the default values:
	 * <ul>
	 * <li>An Eclipse AST tokenizer is used.</li>
	 * <li>The result constructor will use tokens as the unit measure.</li>
	 * <li>The minimum match length is 6.</li>
	 * <li>No model classes are specified.</li>
	 * </ul>
	 * 
	 * @return The default configuration.
	 */
	public static GSTSimilarityCalculationAlgorithmConfiguration getDefaultConfiguration() {
		Tokenizer tokenizer = new EclipseASTTokenizer();
		IGSTResultConstructor resultConstructor = new TokenGSTResultConstructor();
		int minimumMatchLength = 6;

		return new GSTSimilarityCalculationAlgorithmConfiguration(tokenizer, resultConstructor, minimumMatchLength);
	}

	public Tokenizer getTokenizer() {
		return tokenizer;
	}

	public void setTokenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	public IGSTResultConstructor getResultConstructor() {
		return resultConstructor;
	}

	public void setResultConstructor(IGSTResultConstructor resultConstructor) {
		this.resultConstructor = resultConstructor;
	}

	public int getMinimumMatchLength() {
		return minimumMatchLength;
	}

	public void setMinimumMatchLength(int minimumMatchLength) {
		this.minimumMatchLength = minimumMatchLength;
	}
}
