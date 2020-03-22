package similaritycalculation.gst.asttokenizer;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import similaritycalculation.CodeUnit;
import similaritycalculation.gst.TokenValue;

/**
 * Visitor representing the functionality of the fourth pre-processing step in
 * the global similarity calculation process. Some more aggressive, general
 * replacements are performed.
 */
public class EclipseASTTokenVisitorStep4 extends EclipseASTTokenVisitorStep3 {
	/**
	 * Creates a visitor for the fourth step.
	 * 
	 * @param codeUnit
	 *            The code unit.
	 * @param modelClass
	 *            Model class related to the code unit. It can be null, but some
	 *            pre-processing actions may not be effective depending on the
	 *            step.
	 */
	public EclipseASTTokenVisitorStep4(CodeUnit codeUnit, IType modelClass) {
		super(codeUnit, modelClass);
	}

	/**
	 * Normalize the variable types, replacing them with a unique identifier.
	 * 
	 * There are different Type subclasses. Instead of overriding each specific
	 * visit method, we include this behavior in the general preVisit method.
	 */
	@Override
	public boolean preVisit2(ASTNode node) {
		if (node instanceof Type) {
			addToken(node, TokenValue.TYPE);

			return false;
		}
		return super.preVisit2(node);
	}

	/**
	 * Replace variable declarations with a unique identifier.
	 */
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		addToken(node, TokenValue.VARIABLE_DECLARATION);
		return false;
	}

	/**
	 * Replace variable initializations with a unique identifier.
	 */
	@Override
	public boolean visit(ClassInstanceCreation node) {
		addToken(node, TokenValue.CLASS_INSTANCE_CREATION);
		return false;
	}

	/**
	 * Replace method invocations with a unique identifier.
	 */
	@Override
	public boolean visit(MethodInvocation node) {
		addToken(node, TokenValue.METHOD_INVOCATION);
		return false;
	}
}
