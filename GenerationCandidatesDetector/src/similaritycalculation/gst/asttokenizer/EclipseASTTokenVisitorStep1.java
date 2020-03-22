package similaritycalculation.gst.asttokenizer;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import similaritycalculation.CodeUnit;
import similaritycalculation.gst.TokenValue;

/**
 * Visitor representing the functionality of the first pre-processing step in
 * the global similarity calculation process. It includes some basic
 * pre-processing actions to eliminate irrelevant differences without altering
 * much the original behavior of the code.
 */
public class EclipseASTTokenVisitorStep1 extends EclipseASTTokenVisitorDefaultStep {
	/**
	 * Creates a visitor for the first step.
	 * 
	 * @param codeUnit
	 *            The code unit.
	 * @param modelClass
	 *            Model class related to the code unit. It can be null, but some
	 *            pre-processing actions may not be effective depending on the
	 *            step.
	 */
	public EclipseASTTokenVisitorStep1(CodeUnit codeUnit, IType modelClass) {
		super(codeUnit, modelClass);
	}

	/**
	 * Remove import declarations.
	 */
	@Override
	public boolean visit(ImportDeclaration node) {
		return false;
	}

	/**
	 * Remove package declarations.
	 */
	@Override
	public boolean visit(PackageDeclaration node) {
		return false;
	}

	/**
	 * Remove Javadoc comments.
	 */
	@Override
	public boolean visit(Javadoc node) {
		return false;
	}

	/**
	 * Remove comments.
	 */
	@Override
	public boolean visit(BlockComment node) {
		return false;
	}

	/**
	 * Remove comments.
	 */
	@Override
	public boolean visit(LineComment node) {
		return false;
	}

	/**
	 * Remove modifiers.
	 */
	@Override
	public boolean visit(Modifier node) {
		return false;
	}

	/**
	 * Replace with a unique identifier the names of each of the following:
	 * Variables - Type declarations - Parameters - Attributes - Enum constants
	 * 
	 * Keep the names of the methods.
	 */
	@Override
	public boolean visit(SimpleName node) {
		IBinding binding = node.resolveBinding();

		if (binding != null) {
			switch (binding.getKind()) {
			case IBinding.METHOD:
				return super.visit(node);
			case IBinding.VARIABLE:
				IVariableBinding variableBinding = (IVariableBinding) binding;
				if (variableBinding.isField()) {
					addToken(node, TokenValue.FIELD_NAME);
				} else if (variableBinding.isParameter()) {
					addToken(node, TokenValue.PARAMETER_NAME);
				} else if (variableBinding.isEnumConstant()) {
					addToken(node, TokenValue.ENUM_CONSTANT_NAME);
				} else {
					addToken(node, TokenValue.VARIABLE_NAME);
				}
				break;
			case IBinding.TYPE:
				if (node.getParent() instanceof TypeDeclaration) {
					TypeDeclaration parent = (TypeDeclaration) node.getParent();
					TokenValue nodeValue = parent.isInterface() ? TokenValue.INTERFACE_DECLARATION
							: TokenValue.CLASS_DECLARATION;
					addToken(node, nodeValue);
				} else {
					return super.visit(node);
				}
				break;

			default:
				return super.visit(node);
			}
		} else {
			return super.visit(node);
		}
		return false;
	}

	/**
	 * Remove field declarations.
	 */
	@Override
	public boolean visit(FieldDeclaration node) {
		return false;
	}

	/**
	 * Remove simple get and set methods. Remove method parameters.
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		if (isSimpleGetMethod(node)) {
			return false;
		}
		if (isSimpleSetMethod(node)) {
			return false;
		}

		// Default method declaration handling, except that the arguments are
		// not visited any more.

		if (node.getJavadoc() != null) {
			node.getJavadoc().accept(this);
		}
		visitList(node.modifiers());
		if (!node.typeParameters().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeParameters(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		if (node.getReturnType2() != null) {
			node.getReturnType2().accept(this);
		} else if (!node.isConstructor()) {
			addToken(TokenValue.VOID);
		}
		node.getName().accept(this);
		addToken(node, TokenValue.OPENING_PARENTHESIS);
		if (node.getReceiverType() != null) {
			node.getReceiverType().accept(this);
		}
		if (node.getReceiverQualifier() != null) {
			node.getReceiverQualifier().accept(this);
		}
		// visitList(node.parameters(), ",");
		addToken(node, TokenValue.CLOSING_PARENTHESIS);
		visitList(node.extraDimensions());
		if (!node.thrownExceptionTypes().isEmpty()) {
			addToken(TokenValue.THROWS);
			visitList(node.thrownExceptionTypes(), TokenValue.COMMA);
		}
		if (node.getBody() != null) {
			node.getBody().accept(this);
		} else {
			addToken(TokenValue.SEMICOLON);
		}

		return false;
	}

	/**
	 * Remove method arguments.
	 */
	@Override
	public boolean visit(MethodInvocation node) {
		// Implement the default behavior but without pre-processing the
		// arguments.
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			addToken(TokenValue.DOT);
		}
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		node.getName().accept(this);
		addToken(TokenValue.OPENING_PARENTHESIS);
		// visitList(node.arguments(), ",");
		addToken(TokenValue.CLOSING_PARENTHESIS);

		return false;
	}

	/**
	 * Remove method arguments.
	 */
	@Override
	public boolean visit(ConstructorInvocation node) {
		// Implement the default behavior but without pre-processing the
		// arguments.
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		addToken(TokenValue.THIS);
		addToken(TokenValue.OPENING_PARENTHESIS);
		// visitList(node.arguments(), ",");
		addToken(TokenValue.CLOSING_PARENTHESIS);
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * Remove method arguments.
	 */
	@Override
	public boolean visit(SuperConstructorInvocation node) {
		// Implement the default behavior but without pre-processing the
		// arguments.
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			addToken(TokenValue.DOT);
		}
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		addToken(TokenValue.SUPER);
		addToken(TokenValue.OPENING_PARENTHESIS);
		// visitList(node.arguments(), ",");
		addToken(TokenValue.CLOSING_PARENTHESIS);
		addToken(TokenValue.SEMICOLON);

		return false;
	}

	/**
	 * Remove method arguments.
	 */
	@Override
	public boolean visit(SuperMethodInvocation node) {
		// Implement the default behavior but without pre-processing the
		// arguments.
		if (node.getQualifier() != null) {
			node.getQualifier().accept(this);
			addToken(TokenValue.DOT);
		}
		addToken(TokenValue.SUPER);
		addToken(TokenValue.DOT);
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		node.getName().accept(this);
		addToken(TokenValue.OPENING_PARENTHESIS);
		// visitList(node.arguments(), ",");
		addToken(TokenValue.CLOSING_PARENTHESIS);

		return false;
	}

	/**
	 * Remove method arguments.
	 */
	@Override
	public boolean visit(ClassInstanceCreation node) {
		// Implement the default behavior but without pre-processing the
		// arguments.
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			addToken(TokenValue.DOT);
		}
		addToken(TokenValue.NEW);
		if (!node.typeArguments().isEmpty()) {
			addToken(TokenValue.LESS_THAN);
			visitList(node.typeArguments(), TokenValue.COMMA);
			addToken(TokenValue.GREATER_THAN);
		}
		node.getType().accept(this);
		addToken(TokenValue.OPENING_PARENTHESIS);
		// visitList(node.arguments(), ",");
		addToken(TokenValue.CLOSING_PARENTHESIS);
		if (node.getAnonymousClassDeclaration() != null) {
			node.getAnonymousClassDeclaration().accept(this);
		}

		return false;
	}

	/**
	 * Checks if a method is a simple get method, that is, a method that only
	 * returns the value of a field.
	 * 
	 * @param node
	 *            The MethodDeclaration node
	 * @return true or false, as explained
	 */
	private boolean isSimpleGetMethod(MethodDeclaration node) {
		// Identification of simple get methods.
		// The method must meet several conditions.

		// 1. The name must begin with "get" or "is"
		if (!(node.getName().toString().startsWith("get") || node.getName().toString().startsWith("is"))) {
			return false;
		}

		// 2. Must have a return type
		if (node.getReturnType2() == null || node.getReturnType2().toString().equals("void")) {
			return false;
		}

		// 3. Must not have arguments
		if (!node.parameters().isEmpty()) {
			return false;
		}

		// 4. Must have only one statement
		if (node.getBody() == null || node.getBody().statements().size() != 1) {
			return false;
		}

		// 5. The statement must be a return statement
		if (!(node.getBody().statements().get(0) instanceof ReturnStatement)) {
			return false;
		}

		// 6. It must return a field
		ReturnStatement statement = (ReturnStatement) node.getBody().statements().get(0);
		if (!(statement.getExpression() instanceof SimpleName)) {
			return false;
		}
		SimpleName variable = (SimpleName) statement.getExpression();
		IBinding binding = variable.resolveBinding();
		if (binding == null || binding.getKind() != IBinding.VARIABLE) {
			return false;
		}
		IVariableBinding varBinding = (IVariableBinding) binding;
		if (!varBinding.isField()) {
			return false;
		}

		// All the conditions are met: it is a simple get method.

		return true;
	}

	/**
	 * Checks if a method is a simple set method, that is, a method that only
	 * sets the parameter value to a field.
	 * 
	 * @param node
	 *            The MethodDeclaration node
	 * @return true or false, as explained
	 */
	private boolean isSimpleSetMethod(MethodDeclaration node) {
		// Identification of simple set methods.
		// The method must meet several conditions.

		// 1. The method name must begin with "set"
		if (!node.getName().toString().startsWith("set")) {
			return false;
		}

		// 2. Must not have a return type
		if (node.getReturnType2() != null && !node.getReturnType2().toString().equals("void")) {
			return false;
		}

		// 3. Must have exactly one argument
		if (node.parameters().size() != 1) {
			return false;
		}

		// 4. Must have only one statement
		if (node.getBody() == null || node.getBody().statements().size() != 1) {
			return false;
		}

		// 5. The statement must be an expression statement
		if (!(node.getBody().statements().get(0) instanceof ExpressionStatement)) {
			return false;
		}

		// 6. The expression statement must contain an assignment
		ExpressionStatement expression = (ExpressionStatement) node.getBody().statements().get(0);
		if (!(expression.getExpression() instanceof Assignment)) {
			return false;
		}

		// 6. The left side must be a field access
		Assignment assignment = (Assignment) expression.getExpression();
		if (!(assignment.getLeftHandSide() instanceof FieldAccess)) {
			return false;
		}

		// 7. The right side must be a simple name
		if (!(assignment.getRightHandSide() instanceof SimpleName)) {
			return false;
		}

		// 8. And it must be the parameter's name
		SimpleName variable = (SimpleName) assignment.getRightHandSide();
		SimpleName parameter = ((SingleVariableDeclaration) node.parameters().get(0)).getName();
		if (!variable.toString().equals(parameter.toString())) {
			return false;
		}

		// All the conditions are met: it is a simple set method.

		return true;
	}
}
