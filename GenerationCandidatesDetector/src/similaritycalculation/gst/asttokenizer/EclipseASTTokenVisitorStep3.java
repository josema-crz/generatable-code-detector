package similaritycalculation.gst.asttokenizer;

import java.util.List;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import similaritycalculation.CodeUnit;
import similaritycalculation.gst.Token;
import similaritycalculation.gst.TokenValue;

/**
 * Visitor representing the functionality of the third pre-processing step in
 * the global similarity calculation process. It is a generation-specific step
 * where a model class related to the code unit is necessary for the actions.
 * Some more advanced and "unsafe" replacements are performed using this class.
 */
public class EclipseASTTokenVisitorStep3 extends EclipseASTTokenVisitorStep2 {
	/**
	 * Positions of the first and last token of the last processed statement.
	 */
	private int posOldFirstToken, posOldLastToken;
	/**
	 * Positions of the first and last token of the statement being processed.
	 */
	private int posFirstToken, posLastToken;

	/**
	 * Creates a visitor for the third step.
	 * 
	 * @param codeUnit
	 *            The code unit.
	 * @param modelClass
	 *            Model class related to the code unit. It can be null, but some
	 *            pre-processing actions may not be effective depending on the
	 *            step.
	 */
	public EclipseASTTokenVisitorStep3(CodeUnit codeUnit, IType modelClass) {
		super(codeUnit, modelClass);
	}

	/**
	 * Replace all method calls on objects of the model class with a unique
	 * identifier.
	 */
	@Override
	public boolean visit(MethodInvocation node) {
		// We get the declaring class of the method invoked.
		IMethodBinding methodBinding = node.resolveMethodBinding();
		if (methodBinding != null) {
			ITypeBinding typeBinding = methodBinding.getDeclaringClass();
			if (typeBinding != null) {
				// If it is the model class, we replace the whole method
				// invocation with an identifier and stop the processing of the
				// subtree.
				if (modelClass != null && typeBinding.getQualifiedName().equals(modelClass.getFullyQualifiedName())) {
					addToken(node, TokenValue.MODEL_CLASS_METHOD_INVOCATION);
					return false;
				}
			}
		}
		return super.visit(node);
	}

	/**
	 * Remove consecutive duplicated lines of code.
	 * 
	 * We keep a record of the tokens inside the token list that correspond to
	 * the last processed statement and to the currently processed statement.
	 * After finish processing a statement, we compare the tokens generated from
	 * it to ones generated from the previous statement, and delete them if both
	 * sublists are equal.
	 * 
	 * We temporarily only do this with ExpressionStatement, since there is no
	 * nested statements and the process is simpler, but it would be best to
	 * handle any type of Statement.
	 */

	/**
	 * Remove consecutive duplicated lines of code.
	 */
	@Override
	public boolean visit(ExpressionStatement node) {
		// Mark the start of a new statement in the token list.
		posFirstToken = tokenList.size();

		return super.visit(node);
	}

	/**
	 * Remove consecutive duplicated lines of code.
	 */
	@Override
	public void endVisit(ExpressionStatement node) {
		// Mark the end of a new statement in the token list.
		posLastToken = tokenList.size() - 1;

		// If the two last processed statements are consecutive, we compare
		// them.
		if (posOldLastToken != 0 && posOldLastToken == posFirstToken - 1) {
			List<Token> oldStatementTokens = tokenList.subList(posOldFirstToken, posOldLastToken + 1); // inclusive,
																										// exclusive
			List<Token> newStatementTokens = tokenList.subList(posFirstToken, posLastToken + 1); // inclusive,
																									// exclusive

			if (oldStatementTokens.equals(newStatementTokens)) {
				// If the new statement is equal, then we delete the
				// corresponding tokens from the list.
				for (int i = posLastToken; i >= posFirstToken; i--) {
					tokenList.remove(i);
				}
				// Do not update the positions
				return;
			}
		}
		// Update the positions
		posOldFirstToken = posFirstToken;
		posOldLastToken = posLastToken;

		super.endVisit(node);
	}
}
