package similaritycalculation.gst;

import java.util.List;

import org.eclipse.jdt.core.IType;

import similaritycalculation.CodeUnit;

/**
 * This abstract class has to be extended by classes that can be used for
 * retrieving a list of tokens for a particular code unit in a particular step
 * in the global similarity calculation process. The resulting tokenlist can be
 * used to calculate the similarity between two code units in this step.
 */
public abstract class Tokenizer {
	/**
	 * Gets the list of tokens for a specific code unit in a particular step in
	 * the global similarity calculation process.
	 * 
	 * @param codeUnit
	 *            The code unit.
	 * @param modelClass
	 *            Model class related to the code unit. It can be null, but some
	 *            pre-processing actions may not be effective depending on the
	 *            step.
	 * @param step
	 *            The step in the global process.
	 * @return The token list.
	 * @throws TokenizeException
	 *             If the type of code unit or the step specified are not
	 *             supported by the tokenizer.
	 */
	public List<Token> getTokenList(CodeUnit codeUnit, IType modelClass, int step) throws TokenizeException {
		if (!checkCU(codeUnit)) {
			throw new TokenizeException("The code unit type is not supported by this tokenizer.");
		}
		if (!checkStep(step)) {
			throw new TokenizeException("The specified step is not implemented by this tokenizer.");
		}
		List<Token> tokenList = tokenize(codeUnit, modelClass, step);

		return tokenList;
	}

	/**
	 * Checks if the specific type of the code unit is supported by the
	 * tokenizer.
	 * 
	 * @param codeUnit
	 *            The code unit.
	 * @return true if supported, false otherwise.
	 */
	protected abstract boolean checkCU(CodeUnit codeUnit);

	/**
	 * Checks if the specific step is supported by the tokenizer.
	 * 
	 * @param step
	 *            The step.
	 * @return true if supported, false otherwise.
	 */
	protected abstract boolean checkStep(int step);

	/**
	 * Obtains the list of tokens from the code unit for a specific step.
	 * 
	 * @param codeUnit
	 *            The code unit.
	 * @param modelClass
	 *            Model class related to the code unit. It can be null, but some
	 *            pre-processing actions may not be effective depending on the
	 *            step.
	 * @param step
	 *            The step.
	 * @return The list of tokens.
	 */
	protected abstract List<Token> tokenize(CodeUnit codeUnit, IType modelClass, int step);
}
